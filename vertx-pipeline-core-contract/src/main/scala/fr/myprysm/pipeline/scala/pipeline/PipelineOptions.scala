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

import fr.myprysm.pipeline.pipeline.{PipelineOptions => JPipelineOptions}
import io.vertx.core.json.JsonObject
import io.vertx.lang.scala.json.Json._

class PipelineOptions(private val _asJava: JPipelineOptions) {

  def asJava = _asJava

  /**
    * The deploy channel.
    *
    * This address is automatically generated when starting the pipeline
    */
  def setDeployChannel(value: String) = {
    asJava.setDeployChannel(value)
    this
  }
  def getDeployChannel: String = {
    asJava.getDeployChannel().asInstanceOf[String]
  }

  /**
    * The name of the pipeline.
    *
    * This is used to build the pipeline components names.
    * Expected to be camel-case ("my-awesome-pipeline").
    */
  def setName(value: String) = {
    asJava.setName(value)
    this
  }
  def getName: String = {
    asJava.getName().asInstanceOf[String]
  }

  /**
    * The processor set.
    * Mandatory field for each processor is <code>type</code>.
    * Name is automatically generated, you can provide your own one though.
    *
    * See vertx-pipeline-core and other modules for more information about specific ProcessorOptions.
    */
  def setProcessors(value: io.vertx.core.json.JsonArray) = {
    asJava.setProcessors(value)
    this
  }
  def getProcessors: io.vertx.core.json.JsonArray = {
    asJava.getProcessors()
  }

  /**
    * The pump.
    *
    * Mandatory field is <code>type</code>.
    * Name is automatically generated, you can provide your own one though.
    *
    * See vertx-pipeline-core and other modules for more information about specific PumpOptions.
    */
  def setPump(value: io.vertx.core.json.JsonObject) = {
    asJava.setPump(value)
    this
  }
  def getPump: io.vertx.core.json.JsonObject = {
    asJava.getPump()
  }

  /**
    * The sink.
    *
    * Mandatory field is <code>type</code>.
    * Name is automatically generated, you can provide your own one though.
    *
    * See vertx-pipeline-core and other modules for more information about specific SinkOptions.
    */
  def setSink(value: io.vertx.core.json.JsonObject) = {
    asJava.setSink(value)
    this
  }
  def getSink: io.vertx.core.json.JsonObject = {
    asJava.getSink()
  }
}

object PipelineOptions {

  def apply() = {
    new PipelineOptions(new JPipelineOptions(emptyObj()))
  }

  def apply(t: JPipelineOptions) = {
    if (t != null) {
      new PipelineOptions(t)
    } else {
      new PipelineOptions(new JPipelineOptions(emptyObj()))
    }
  }

  def fromJson(json: JsonObject): PipelineOptions = {
    if (json != null) {
      new PipelineOptions(new JPipelineOptions(json))
    } else {
      new PipelineOptions(new JPipelineOptions(emptyObj()))
    }
  }
}
