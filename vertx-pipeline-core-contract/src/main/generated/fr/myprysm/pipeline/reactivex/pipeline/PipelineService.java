/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package fr.myprysm.pipeline.reactivex.pipeline;

import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import fr.myprysm.pipeline.pipeline.PipelineDeployment;
import java.util.Set;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import fr.myprysm.pipeline.pipeline.PipelineOptions;

/**
 * The Pipeline Service is the centric service.
 * <p>
 * It provides the capability
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.pipeline.PipelineService original} non RX-ified interface using Vert.x codegen.
 */

@io.vertx.lang.reactivex.RxGen(fr.myprysm.pipeline.pipeline.PipelineService.class)
public class PipelineService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PipelineService that = (PipelineService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final io.vertx.lang.reactivex.TypeArg<PipelineService> __TYPE_ARG = new io.vertx.lang.reactivex.TypeArg<>(
    obj -> new PipelineService((fr.myprysm.pipeline.pipeline.PipelineService) obj),
    PipelineService::getDelegate
  );

  private final fr.myprysm.pipeline.pipeline.PipelineService delegate;
  
  public PipelineService(fr.myprysm.pipeline.pipeline.PipelineService delegate) {
    this.delegate = delegate;
  }

  public fr.myprysm.pipeline.pipeline.PipelineService getDelegate() {
    return delegate;
  }

  /**
   * Get the nodes available
   * @param handler 
   */
  public void getNodes(Handler<AsyncResult<Set<String>>> handler) { 
    delegate.getNodes(handler);
  }

  /**
   * Get the nodes available
   * @return 
   */
  public Single<Set<String>> rxGetNodes() { 
    return new io.vertx.reactivex.core.impl.AsyncResultSingle<Set<String>>(handler -> {
      getNodes(handler);
    });
  }

  /**
   * Get the running pipelines across all the instances.
   * @param handler the handler
   */
  public void getRunningPipelines(Handler<AsyncResult<Set<PipelineDeployment>>> handler) { 
    delegate.getRunningPipelines(handler);
  }

  /**
   * Get the running pipelines across all the instances.
   * @return 
   */
  public Single<Set<PipelineDeployment>> rxGetRunningPipelines() { 
    return new io.vertx.reactivex.core.impl.AsyncResultSingle<Set<PipelineDeployment>>(handler -> {
      getRunningPipelines(handler);
    });
  }

  /**
   * Get the description of the pipeline identified by the provided deployment information.
   * @param deployment the deployment information
   * @param handler the handler
   */
  public void getPipelineDescription(PipelineDeployment deployment, Handler<AsyncResult<PipelineOptions>> handler) { 
    delegate.getPipelineDescription(deployment, handler);
  }

  /**
   * Get the description of the pipeline identified by the provided deployment information.
   * @param deployment the deployment information
   * @return 
   */
  public Single<PipelineOptions> rxGetPipelineDescription(PipelineDeployment deployment) { 
    return new io.vertx.reactivex.core.impl.AsyncResultSingle<PipelineOptions>(handler -> {
      getPipelineDescription(deployment, handler);
    });
  }

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
   * @param options the pipeline configuration
   * @param node the node to start the pipeline. can be null.
   * @param handler the handler
   */
  public void startPipeline(PipelineOptions options, String node, Handler<AsyncResult<PipelineDeployment>> handler) { 
    delegate.startPipeline(options, node, handler);
  }

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
   * @param options the pipeline configuration
   * @param node the node to start the pipeline. can be null.
   * @return 
   */
  public Single<PipelineDeployment> rxStartPipeline(PipelineOptions options, String node) { 
    return new io.vertx.reactivex.core.impl.AsyncResultSingle<PipelineDeployment>(handler -> {
      startPipeline(options, node, handler);
    });
  }

  /**
   * Stops the pipeline from the provided deployment.
   * <p>
   * Emits a signal when operation is complete.
   * @param deployment the deployment information of the pipeline to stop.
   * @param handler the handler
   */
  public void stopPipeline(PipelineDeployment deployment, Handler<AsyncResult<Void>> handler) { 
    delegate.stopPipeline(deployment, handler);
  }

  /**
   * Stops the pipeline from the provided deployment.
   * <p>
   * Emits a signal when operation is complete.
   * @param deployment the deployment information of the pipeline to stop.
   * @return 
   */
  public Completable rxStopPipeline(PipelineDeployment deployment) { 
    return new io.vertx.reactivex.core.impl.AsyncResultCompletable(handler -> {
      stopPipeline(deployment, handler);
    });
  }


  public static  PipelineService newInstance(fr.myprysm.pipeline.pipeline.PipelineService arg) {
    return arg != null ? new PipelineService(arg) : null;
  }
}
