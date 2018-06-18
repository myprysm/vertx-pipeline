/*
 * Copyright (c) 2014 Red Hat, Inc. and others
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

package fr.myprysm.pipeline.pipeline;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link fr.myprysm.pipeline.pipeline.PipelineDeployment}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.pipeline.PipelineDeployment} original class using Vert.x codegen.
 */
public class PipelineDeploymentConverter {

  public static void fromJson(JsonObject json, PipelineDeployment obj) {
    if (json.getValue("controlChannel") instanceof String) {
      obj.setControlChannel((String)json.getValue("controlChannel"));
    }
    if (json.getValue("name") instanceof String) {
      obj.setName((String)json.getValue("name"));
    }
  }

  public static void toJson(PipelineDeployment obj, JsonObject json) {
    if (obj.getControlChannel() != null) {
      json.put("controlChannel", obj.getControlChannel());
    }
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
  }
}