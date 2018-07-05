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

import fr.myprysm.pipeline.pipeline.{PipelineDeployment => JPipelineDeployment}
import io.vertx.core.json.JsonObject
import io.vertx.lang.scala.json.Json._

/**
  * Response provided when a pipeline is successfully deployed.
  */
class PipelineDeployment(private val _asJava: JPipelineDeployment) {

  def asJava = _asJava

  /**
    * The pipeline control channel address.
    *
    * This address allows to communicate signals to this particular pipeline and its components.
    *
    * See vertx-pipeline-core about supported <code>Signal</code>s.
    */
  def setControlChannel(value: String) = {
    asJava.setControlChannel(value)
    this
  }
  def getControlChannel: String = {
    asJava.getControlChannel().asInstanceOf[String]
  }

  /**
    * The pipeline identifier.
    *
    * This is basically the deployment ID for the <code>PipelineVerticle</code>.
    *
    * This is a read-only property
    */
  def setId(value: String) = {
    asJava.setId(value)
    this
  }
  def getId: String = {
    asJava.getId().asInstanceOf[String]
  }

  /**
    * The pipeline name.
    *
    * This is a normalized name (kebab-cased).
    */
  def setName(value: String) = {
    asJava.setName(value)
    this
  }
  def getName: String = {
    asJava.getName().asInstanceOf[String]
  }

  /**
    * The pipeline node identifier.
    *
    * This is the node that hosts the pipeline in cluster mode.
    */
  def setNode(value: String) = {
    asJava.setNode(value)
    this
  }
  def getNode: String = {
    asJava.getNode().asInstanceOf[String]
  }
}

object PipelineDeployment {

  def apply() = {
    new PipelineDeployment(new JPipelineDeployment(emptyObj()))
  }

  def apply(t: JPipelineDeployment) = {
    if (t != null) {
      new PipelineDeployment(t)
    } else {
      new PipelineDeployment(new JPipelineDeployment(emptyObj()))
    }
  }

  def fromJson(json: JsonObject): PipelineDeployment = {
    if (json != null) {
      new PipelineDeployment(new JPipelineDeployment(json))
    } else {
      new PipelineDeployment(new JPipelineDeployment(emptyObj()))
    }
  }
}
