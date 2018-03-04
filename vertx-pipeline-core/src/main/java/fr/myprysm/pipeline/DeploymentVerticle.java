/*
 * Copyright 2018 the original author or the original authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.myprysm.pipeline;


import fr.myprysm.pipeline.pipeline.ExchangeOptions;
import fr.myprysm.pipeline.pipeline.PipelineVerticle;
import fr.myprysm.pipeline.util.ClasspathHelpers;
import fr.myprysm.pipeline.util.Signal;
import fr.myprysm.pipeline.util.SignalReceiver;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.CompletableHelper;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Objects;
import java.util.UUID;

import static io.reactivex.Completable.*;
import static io.reactivex.Observable.fromIterable;

public class DeploymentVerticle extends AbstractVerticle implements SignalReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(DeploymentVerticle.class);
    public static final String UNDEPLOY = "undeploy";
    public static final String PIPELINE_VERTICLE = "fr.myprysm.pipeline.pipeline.PipelineVerticle";
    public static final String DEPLOYMENT_ERROR = "Unable to deploy pipeline";

    private LinkedList<Pair<String, String>> pipelineDeployments = new LinkedList<>();
    private String deployChannel = UUID.randomUUID().toString();
    private EventBus eventBus;
    private MessageConsumer<String> consumer;

    private static boolean runningPipeline(Pair<String, String> dep) {
        return !DEPLOYMENT_ERROR.equals(dep.getValue());
    }

    @Override
    public void start(Future<Void> started) {
        readConfiguration()
                .map(this::prepareConfiguration)
                .flatMap(this::loadClasses)
                .flatMapCompletable(this::startPipelines)
                .subscribe(CompletableHelper.toObserver(started));
    }

    @Override
    public void stop(Future<Void> stopped) throws Exception {
        consumer.rxUnregister().subscribe(CompletableHelper.toObserver(stopped));
    }

    /**
     * Prepares the pipeline name by extracting it from configuratino
     *
     * @param json the global pipelines configuration
     * @return the configuration with each pipeline named. according to its entry name from configuration
     */
    private JsonObject prepareConfiguration(JsonObject json) {
        eventBus = vertx.eventBus();
        consumer = eventBus.<String>consumer(deployChannel, this::handleSignal);
        json.fieldNames().forEach(name ->
                json.getJsonObject(name)
                        .put("name", name)
                        .put("deployChannel", deployChannel)
        );
        return json;
    }

    private void handleSignal(Message<String> signal) {
        String action = signal.headers().get("action");
        String pipeline = signal.body();
        if (UNDEPLOY.equals(action) && pipeline != null) {
            fromIterable(pipelineDeployments)
                    .filter(deploy -> deploy.getLeft().equals(pipeline))
                    .flatMapCompletable(this::stopPipeline)
                    .andThen(defer(this::hasRunningPipelines))
                    .doOnError(throwable -> {
                        if (throwable instanceof DeploymentException) {
                            vertx.close();
                        }
                    })
                    .subscribe(
                            () -> LOG.info("Undeployed [{}].", pipeline),
                            throwable -> LOG.error("Shutting down the system.")
                    );
        }
    }

    private Completable stopPipeline(Pair<String, String> pipeline) {
        LOG.error("Undeploying {}:{}", pipeline.getLeft(), pipeline.getRight());
        pipelineDeployments.remove(pipeline);
        return vertx.rxUndeploy(pipeline.getRight());
    }

    private Completable startPipelines(JsonObject config) {
        return fromIterable(config.fieldNames())
                .map(config::getJsonObject)
                .filter(Objects::nonNull)
                .map(this::startPipeline)
                .reduceWith(this::getPipelineDeployments, (list, deployment) -> {
                    deployment.subscribe((Consumer<Pair<String, String>>) list::add);
                    return list;
                }).toCompletable();
        //.andThen(defer(this::hasRunningPipelines));

    }

    private Completable hasRunningPipelines() {
        Boolean deployed = pipelineDeployments.stream()
                .anyMatch(DeploymentVerticle::runningPipeline);
        if (!deployed) {
            return error(new DeploymentException("No running pipeline... Shutting down."));
        }

        return complete();
    }

    /**
     * Starts a pipeline with provided config.
     * <p>
     * If {@link PipelineVerticle} deployment fails, then the error is kept internally but
     * <b>DOES NOT STOP</b> the global deployment of the other pipelines.
     *
     * @param config the config to inject into the {@link PipelineVerticle}
     * @return a single containing a pair with the name of the pipeline and its deployment ID if any.
     */
    private Single<Pair<String, String>> startPipeline(JsonObject config) {
        return vertx.rxDeployVerticle(PIPELINE_VERTICLE, new DeploymentOptions().setConfig(config))
                .map(id -> Pair.of(config.getString("name"), id))
                .doOnSuccess(dep -> LOG.info("Deployed pipeline [{}]: {}", dep.getLeft(), dep.getRight()))
                .onErrorResumeNext((throwable) -> Single.just(Pair.of(config.getString("name"), DEPLOYMENT_ERROR)));
    }


    /**
     * Loads the configuration from a file to prepare all the pipelines.
     *
     * @return the loaded configuration as a {@link Single}
     */
    private Single<JsonObject> readConfiguration() {
        ConfigStoreOptions store = new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject()
                        .put("path", "*config.yml")
                );

        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
                        .addStore(store)
//                .setIncludeDefaultStores(true)
        );

        return retriever.rxGetConfig();
    }

    /**
     * Get the list of pipeline deployments holded internally.
     *
     * @return the list of pipeline deployments
     */
    public LinkedList<Pair<String, String>> getPipelineDeployments() {
        return pipelineDeployments;
    }

    /**
     * Load classes asynchronously as it is a blocking operation.
     *
     * @param config the config
     * @return a single with the config
     */
    private Single<JsonObject> loadClasses(JsonObject config) {
        return vertx.rxExecuteBlocking(future -> {
            ClasspathHelpers.getScan();
            future.complete(config);
        });
    }

    /**
     * Specific case for this verticle the deployChannel IS the controlChannel.
     *
     * @return the deployChannel.
     */
    @Override
    public String controlChannel() {
        return deployChannel;
    }

    /**
     * As this verticle is the control tower over the configured and deployed pipelines
     * its does not communicate the same informations on the same channels
     * even though it implements some of the interfaces.
     * <p>
     * This handler will never be called.
     *
     * @param signal the signal received.
     * @return always complete
     */
    @Override
    public Completable onSignal(Signal signal) {
        return complete();
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
