package fr.myprysm.pipeline.pipeline;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

/**
 * The Pipeline Service is the centric service.
 * <p>
 * It provides the capability
 */
@VertxGen
@ProxyGen
public interface PipelineService {


    /**
     * Get the running pipelines across all the instances.
     * <p>
     * This is a complete description of all the pipeline with their options.
     * Please take care when using this as when to many pipelines are deployed
     * this can lead either to an {@link OutOfMemoryError} or to a communication failure
     * as the description is too large to be emitted through the event bus.
     *
     * @param handler the handler
     */
    void getRunningPipelines(Handler<AsyncResult<List<PipelineOptions>>> handler);


    /**
     * Starts a pipeline with the provided configuration.
     * <p>
     * Please note that the pipeline name must be unique across all the instances.
     * <p>
     * When running in cluster mode, the service will try to find an appropriate node to start the pipeline.
     * This allows to run now data flows from nodes that are not currently hosting the components.
     * <p>
     * Response contains the normalized name with the control channel to communicate through signals
     * with the deployed pipeline.
     *
     * @param options the pipeline configuration
     * @param handler the handler
     */
    void startPipeline(PipelineOptions options, Handler<AsyncResult<PipelineDeployment>> handler);

    /**
     * Stops the pipeline identified by the provided name.
     * <p>
     * Emits a signal when operation is complete.
     *
     * @param name    the name of the pipeline to stop.
     * @param handler the handler
     */
    void stopPipeline(String name, Handler<AsyncResult<Void>> handler);
}
