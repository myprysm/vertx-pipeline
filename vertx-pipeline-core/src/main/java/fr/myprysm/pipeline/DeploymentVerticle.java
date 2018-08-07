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

package fr.myprysm.pipeline;


import fr.myprysm.pipeline.metrics.MetricsProvider;
import fr.myprysm.pipeline.pipeline.PipelineOptions;
import fr.myprysm.pipeline.pipeline.impl.PipelineServiceImpl;
import fr.myprysm.pipeline.reactivex.pipeline.PipelineService;
import fr.myprysm.pipeline.spi.MetricsServiceFactory;
import fr.myprysm.pipeline.util.ClasspathHelpers;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.ServiceHelper;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.CompletableHelper;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.UUID;

import static io.reactivex.Observable.fromIterable;

/**
 * Verticle that handles the deployment of the pipelines configured in the YAML file provided at <code>path</code>.
 * <p>
 * Starts a {@link PipelineService} that will handle the deployments.
 */
@Slf4j
public class DeploymentVerticle extends AbstractVerticle {

    /**
     * Component name.
     */
    static final String NAME = "deployment-verticle";

    /**
     * Property for configuration file path.
     */
    private static final String CONFIG_PATH = "path";

    /**
     * The deploy channel.
     */
    private final String deployChannel = UUID.randomUUID().toString();
    /**
     * Component instance name.
     */
    private final String name = NAME + ":" + deployChannel;
    /**
     * Configuration file path.
     */
    private String path;
    /**
     * Pipeline Service instance.
     */
    private PipelineServiceImpl pipelineService;

    /**
     * Service RX version.
     */
    private PipelineService rxService;

    /**
     * Message consumer for the {@link fr.myprysm.pipeline.pipeline.PipelineService} proxy.
     */
    private MessageConsumer<JsonObject> serviceConsumer;

    @Override
    public void start(Future<Void> started) {
        path = config().getString(CONFIG_PATH, "config.yml");
        startPipelineService()
                .andThen(Single.defer(this::readConfiguration))
                .map(this::prepareConfiguration)
                .flatMap(this::loadClasses)
                .map(this::initializeMetrics)
                .flatMapCompletable(this::startPipelines)
                .subscribe(CompletableHelper.toObserver(started));
    }

    /**
     * Starts the pipeline service.
     * <p>
     * Registers a proxy handler on the service.
     *
     * @return a completable that finishes when pipeline service is started.
     */
    private Completable startPipelineService() {
        pipelineService = new PipelineServiceImpl(vertx, deployChannel);
        rxService = new PipelineService(pipelineService);
        serviceConsumer = new ServiceBinder(vertx.getDelegate())
                .setAddress(fr.myprysm.pipeline.pipeline.PipelineService.ADDRESS)
                .register(fr.myprysm.pipeline.pipeline.PipelineService.class, pipelineService);
        return pipelineService.configure();
    }

    /**
     * Initialize the metrics when an implementation is provided.
     *
     * @param config the configuration
     * @return the configuration
     */
    private JsonObject initializeMetrics(JsonObject config) {
        DeploymentVerticleOptions opts = new DeploymentVerticleOptions(config());
        if (opts.getMetrics()) {
            MetricsServiceFactory factory = ServiceHelper.loadFactoryOrNull(MetricsServiceFactory.class);
            if (factory == null) {
                log.info("Requested metrics but no factory found on the classpath?!");
            } else {
                MetricsProvider.initialize(factory.create(opts));
            }
        }

        return config;
    }

    @Override
    public void stop(Future<Void> stopped) {
        MetricsProvider.close();
        serviceConsumer.unregister();
        pipelineService.close(false) // all the verticles will be stopped when this one will be stopped.
                .doOnComplete(() -> log.info("{} stopped.", name))
                .subscribe(CompletableHelper.toObserver(stopped));
    }

    /**
     * Prepares the pipeline name by extracting it from configuration.
     *
     * @param json the global pipelines configuration
     * @return the configuration with each pipeline named. according to its entry name from configuration
     */
    private JsonObject prepareConfiguration(JsonObject json) {
        json.fieldNames().forEach(pipeline ->
                json.getJsonObject(pipeline)
                        .put("name", pipeline)
                        .put("deployChannel", deployChannel)
        );
        return json;
    }

    /**
     * Start the configured pipelines.
     *
     * @param config the configuration
     * @return completable that finishes successfully when all the pipelines are started.
     */
    private Completable startPipelines(JsonObject config) {
        return fromIterable(config.fieldNames())
                .map(config::getJsonObject)
                .filter(Objects::nonNull)
                .flatMapCompletable(this::startPipeline);
    }

    /**
     * Starts a pipeline with provided config.
     * <p>
     * <s>
     * If {@link fr.myprysm.pipeline.pipeline.PipelineVerticle} deployment fails, then the error is kept internally but
     * <b>DOES NOT STOP</b> the global deployment of the other pipelines.
     *
     * @param config the config to inject into the {@link fr.myprysm.pipeline.pipeline.PipelineVerticle}
     * @return a single containing a pair with the name of the pipeline and its deployment ID if any.
     */
    private Completable startPipeline(JsonObject config) {
        return rxService.rxStartPipeline(new PipelineOptions(config), deployChannel).toCompletable();
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
                        .put("path", path)
                );

        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
                        .addStore(store)
//                .setIncludeDefaultStores(true)
        );

        return retriever.rxGetConfig();
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

}
