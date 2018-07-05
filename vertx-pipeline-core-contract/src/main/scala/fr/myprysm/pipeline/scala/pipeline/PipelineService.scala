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

package fr.myprysm.pipeline.scala.pipeline

import fr.myprysm.pipeline.pipeline.{PipelineDeployment => JPipelineDeployment, PipelineOptions => JPipelineOptions, PipelineService => JPipelineService}
import io.vertx.core.{AsyncResult, Handler}
import io.vertx.lang.scala.AsyncResultWrapper
import io.vertx.lang.scala.HandlerOps._

import scala.collection.JavaConverters._

/**
  * The Pipeline Service is the centric service.
  *
  * It provides the capability
  */
class PipelineService(private val _asJava: Object) {

  def asJava = _asJava


  /**
    * Get the running pipelines across all the instances.
    *
    * @param handler the handler
    */
  def getRunningPipelines(handler: Handler[AsyncResult[scala.collection.mutable.Set[PipelineDeployment]]]): Unit = {
    asJava.asInstanceOf[JPipelineService].getRunningPipelines({ x: AsyncResult[java.util.Set[JPipelineDeployment]] => handler.handle(AsyncResultWrapper[java.util.Set[JPipelineDeployment], scala.collection.mutable.Set[PipelineDeployment]](x, a => a.asScala.map(x => PipelineDeployment(x)))) })
  }

  /**
    * Get the description of the pipeline identified by the provided deployment information.
    *
    * @param deployment the deployment informationsee <a href="../../../../../../../cheatsheet/PipelineDeployment.html">PipelineDeployment</a>
    * @param handler the handler
    */
  def getPipelineDescription(deployment: PipelineDeployment, handler: Handler[AsyncResult[PipelineOptions]]): Unit = {
    asJava.asInstanceOf[JPipelineService].getPipelineDescription(deployment.asJava, { x: AsyncResult[JPipelineOptions] => handler.handle(AsyncResultWrapper[JPipelineOptions, PipelineOptions](x, a => PipelineOptions(a)))})
  }

  /**
    * Starts a pipeline with the provided configuration.
    * 
    * Please note that the pipeline name must be unique across all the instances.
    * 
    * When running in cluster mode, the service will try to find an appropriate node to start the pipeline.
    * This allows to run now data flows from nodes that are not currently hosting the components.
    * 
    * Response contains the normalized name with the control channel to communicate through signals
    * with the deployed pipeline.
    *
    * @param options the pipeline configurationsee <a href="../../../../../../../cheatsheet/PipelineOptions.html">PipelineOptions</a>
    * @param handler the handler
    */
  def startPipeline(options: PipelineOptions, handler: Handler[AsyncResult[PipelineDeployment]]): Unit = {
    asJava.asInstanceOf[JPipelineService].startPipeline(options.asJava, { x: AsyncResult[JPipelineDeployment] => handler.handle(AsyncResultWrapper[JPipelineDeployment, PipelineDeployment](x, a => PipelineDeployment(a)))})
  }

  /**
    * Stops the pipeline from the provided deployment.
    * 
    * Emits a signal when operation is complete.
    *
    * @param deployment the deployment information of the pipeline to stop.see <a href="../../../../../../../cheatsheet/PipelineDeployment.html">PipelineDeployment</a>
    * @param handler the handler
    */
  def stopPipeline(deployment: PipelineDeployment, handler: Handler[AsyncResult[Unit]]): Unit = {
    asJava.asInstanceOf[JPipelineService].stopPipeline(deployment.asJava, { x: AsyncResult[Void] => handler.handle(AsyncResultWrapper[Void, Unit](x, a => a)) })
  }

  /**
    * Like [[getRunningPipelines]] but returns a [[scala.concurrent.Future]] instead of taking an AsyncResultHandler.
    */
  def getRunningPipelinesFuture(): scala.concurrent.Future[scala.collection.mutable.Set[PipelineDeployment]] = {
    val promiseAndHandler = handlerForAsyncResultWithConversion[java.util.Set[JPipelineDeployment], scala.collection.mutable.Set[PipelineDeployment]](x => x.asScala.map(x => PipelineDeployment(x)))
    asJava.asInstanceOf[JPipelineService].getRunningPipelines(promiseAndHandler._1)
    promiseAndHandler._2.future
  }

  /**
    * Like [[getPipelineDescription]] but returns a [[scala.concurrent.Future]] instead of taking an AsyncResultHandler.
    */
  def getPipelineDescriptionFuture(deployment: PipelineDeployment): scala.concurrent.Future[PipelineOptions] = {
    val promiseAndHandler = handlerForAsyncResultWithConversion[JPipelineOptions, PipelineOptions](x => PipelineOptions(x))
    asJava.asInstanceOf[JPipelineService].getPipelineDescription(deployment.asJava, promiseAndHandler._1)
    promiseAndHandler._2.future
  }

  /**
    * Like [[startPipeline]] but returns a [[scala.concurrent.Future]] instead of taking an AsyncResultHandler.
   */
  def startPipelineFuture(options: PipelineOptions): scala.concurrent.Future[PipelineDeployment] = {
    val promiseAndHandler = handlerForAsyncResultWithConversion[JPipelineDeployment, PipelineDeployment](x => PipelineDeployment(x))
    asJava.asInstanceOf[JPipelineService].startPipeline(options.asJava, promiseAndHandler._1)
    promiseAndHandler._2.future
  }

  /**
    * Like [[stopPipeline]] but returns a [[scala.concurrent.Future]] instead of taking an AsyncResultHandler.
    */
  def stopPipelineFuture(deployment: PipelineDeployment): scala.concurrent.Future[Unit] = {
    val promiseAndHandler = handlerForAsyncResultWithConversion[Void, Unit](x => x)
    asJava.asInstanceOf[JPipelineService].stopPipeline(deployment.asJava, promiseAndHandler._1)
    promiseAndHandler._2.future
  }

}

object PipelineService {
  def apply(asJava: JPipelineService) = new PipelineService(asJava)  
}
