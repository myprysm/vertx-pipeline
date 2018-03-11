/*
 * Copyright 2018 the original author or the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.myprysm.pipeline.pipeline;

import fr.myprysm.pipeline.util.ConfigurableVerticle;
import fr.myprysm.pipeline.util.Signal;
import fr.myprysm.pipeline.util.SignalEmitter;
import fr.myprysm.pipeline.util.SignalReceiver;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Notification;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static io.reactivex.Completable.complete;
import static io.reactivex.Completable.defer;
import static io.reactivex.Observable.fromIterable;
import static strman.Strman.toKebabCase;

public class PipelineVerticle extends ConfigurableVerticle<PipelineOptions> implements SignalEmitter, SignalReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(PipelineVerticle.class);
    /**
     * Pair with on the left side the name of the {@link fr.myprysm.pipeline.pump.Pump}
     * and on the right side the deployment ID
     */
    private Pair<String, String> pumpDeployment;
    private LinkedList<List<Pair<String, String>>> processorsDeployment = new LinkedList<>();

    /**
     * Pair with on the left side the name of the {@link fr.myprysm.pipeline.sink.Sink}
     * and on the right side the deployment ID
     */
    private Pair<String, String> sinkDeployment;

    private EventBus eventBus;

    private String name;
    private PipelineConfigurer config;
    private String deployChannel;
    private String controlChannel;
    private MessageConsumer<String> controlChannelConsumer;

    @Override
    public ValidationResult validate(JsonObject config) {
        return PipelineOptionsValidation.validate(config);
    }

    @Override
    public PipelineOptions readConfiguration(JsonObject config) {
        return new PipelineOptions(config);
    }

    @Override
    public Completable configure(PipelineOptions config) {
        this.name = toKebabCase(config.getName());
        this.config = new PipelineConfigurer(config);
        deployChannel = this.config.getDeployChannel();
        controlChannel = this.config.getControlChannel();
        eventBus = vertx.eventBus();
        controlChannelConsumer = eventBus.consumer(controlChannel, this::handleSignal);
        return complete();
    }

    private void handleSignal(Message<String> signalString) {
        Signal signal = Signal.valueOf(signalString.body());
        switch (signal) {
            case TERMINATE:
                this.undeploy().andThen(defer(this::notifyUndeploy)).subscribe(
                        () -> debug("Undeployed pipeline."),
                        (throwable -> error("An error occured during undeployment", throwable))
                );
        }
    }

    private Completable notifyUndeploy() {
        eventBus().publish(deployChannel, name, new DeliveryOptions().addHeader("action", "undeploy"));
        return complete();
    }


    @Override
    protected Completable startVerticle() {
        return this.startSink()
                .andThen(defer(this::startProcessors))
                .andThen(defer(this::startPump));
    }

    @Override
    protected Logger delegate() {
        return LOG;
    }

    private Completable startPump() {
        return startVerticle(config.getPumpDeployment())
                .doOnSuccess(this::setPumpDeployment)
                .toCompletable();
    }

    private Completable startProcessors() {
        LinkedList<List<Triple<String, String, DeploymentOptions>>> deployments = config.getProcessorDeployments();
        if (deployments.isEmpty()) {
            return complete();
        }

        return fromIterable(deployments)
                .doOnEach(group -> debug("Deploying processor group..."))
                .map(this::deployGroup)
                .doOnEach(group -> debug("Deployed processor group."))
                .flatMapCompletable(item -> item.doOnSuccess(this::addProcessorDeployment).toCompletable());
    }

    private Single<List<Pair<String, String>>> deployGroup(List<Triple<String, String, DeploymentOptions>> group) {
        return fromIterable(group)
                .doOnEach(this::logDeployProcessor)
                .flatMapSingle(this::startVerticle)
                .collect(ArrayList::new, (list, deployment) -> {
                    debug("Component [{}]: {}", deployment.getLeft(), deployment.getRight());
                    list.add(deployment);
                });


    }

    private Completable startSink() {
        return startVerticle(config.getSinkDeployment())
                .doOnSuccess(this::setSinkDeployment)
                .toCompletable();
    }


    private Single<Pair<String, String>> startVerticle(Triple<String, String, DeploymentOptions> deployment) {
        return vertx.rxDeployVerticle(deployment.getMiddle(), deployment.getRight())
                .map(id -> Pair.of(deployment.getLeft(), id))
                .doOnSuccess(dep -> info("Deployed component [{}]...", dep.getRight()));
    }


    @Override
    public Completable shutdown() {
        info("Shutting down...");
        return controlChannelConsumer.rxUnregister();
    }

    private Completable undeploy() {
        return vertx.rxUndeploy(pumpDeployment.getValue())
                .doOnComplete(() -> info("Shutting down processors"))
                .andThen(defer(this::undeployProcessors))
                .onErrorComplete()
                .andThen(vertx.rxUndeploy(sinkDeployment.getValue()));
    }

    private Completable undeployProcessors() {
        return fromIterable(processorsDeployment)
                .doOnEach(group -> debug("Undeploying processor group..."))
                .map(this::undeployGroup)
                .doOnEach(group -> debug("Undeployed processor group."))
                .flatMapCompletable(Completable::onErrorComplete);
    }


    private Completable undeployGroup(List<Pair<String, String>> group) {
        return fromIterable(group)
                .map(this::componentId)
                .doOnError((throwable) -> error("An error occured during processors shutdown: ", throwable))
                .flatMapCompletable(vertx::rxUndeploy);
    }

    /**
     * Logs the name of the component and returns its ID.
     *
     * @param deployment the pair &lt;name, id&gt;
     * @return the id
     */
    private String componentId(Pair<String, String> deployment) {
        debug("Undeploying [{}]...", deployment.getKey());
        return deployment.getValue();
    }

    private void setPumpDeployment(Pair<String, String> pumpDeployment) {
        this.pumpDeployment = pumpDeployment;
    }

    private void addProcessorDeployment(List<Pair<String, String>> processorDeployment) {
        this.processorsDeployment.add(processorDeployment);
    }

    private void setSinkDeployment(Pair<String, String> sinkDeployment) {
        this.sinkDeployment = sinkDeployment;
    }

    private void logDeployProcessor(Notification<Triple<String, String, DeploymentOptions>> deploy) {
        if (deploy.isOnNext()) info("Deploying processor {}...", deploy.getValue().getLeft());
    }

    @Override
    public String controlChannel() {
        return controlChannel;
    }

    @Override
    public Completable onSignal(Signal signal) {
        return complete();
    }

    @Override
    public void emitSignal(Signal signal) {
        // Does nothing
    }

    @Override
    public ExchangeOptions exchange() {
        return null;
    }

    @Override
    public EventBus eventBus() {
        return eventBus;
    }
}
