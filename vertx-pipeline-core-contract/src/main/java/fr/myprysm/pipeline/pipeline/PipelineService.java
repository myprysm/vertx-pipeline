package fr.myprysm.pipeline.pipeline;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.Set;

/**
 * The Pipeline Service is the centric service.
 * <p>
 * It provides the capability
 */
@VertxGen
@ProxyGen
public interface PipelineService {
    String ADDRESS = "fr.myprysm.pipeline:pipeline-service";

    /**
     * Get the nodes available
     *
     * @param handler
     */
    void getNodes(Handler<AsyncResult<Set<String>>> handler);

    /**
     * Get the running pipelines across all the instances.
     *
     * @param handler the handler
     */
    void getRunningPipelines(Handler<AsyncResult<Set<PipelineDeployment>>> handler);

    /**
     * Get the description of the pipeline identified by the provided deployment information.
     *
     * @param deployment the deployment information
     * @param handler    the handler
     */
    void getPipelineDescription(PipelineDeployment deployment, Handler<AsyncResult<PipelineOptions>> handler);


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
     * @param node    the node to start the pipeline. can be null.
     * @param handler the handler
     */
    void startPipeline(PipelineOptions options, String node, Handler<AsyncResult<PipelineDeployment>> handler);

    /**
     * Stops the pipeline from the provided deployment.
     * <p>
     * Emits a signal when operation is complete.
     *
     * @param deployment the deployment information of the pipeline to stop.
     * @param handler    the handler
     */
    void stopPipeline(PipelineDeployment deployment, Handler<AsyncResult<Void>> handler);
}
