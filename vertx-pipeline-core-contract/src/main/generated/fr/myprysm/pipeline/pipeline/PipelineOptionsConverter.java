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
 * Converter for {@link fr.myprysm.pipeline.pipeline.PipelineOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.pipeline.PipelineOptions} original class using Vert.x codegen.
 */
public class PipelineOptionsConverter {

  public static void fromJson(JsonObject json, PipelineOptions obj) {
    if (json.getValue("deployChannel") instanceof String) {
      obj.setDeployChannel((String)json.getValue("deployChannel"));
    }
    if (json.getValue("name") instanceof String) {
      obj.setName((String)json.getValue("name"));
    }
    if (json.getValue("processors") instanceof JsonArray) {
      obj.setProcessors(((JsonArray)json.getValue("processors")).copy());
    }
    if (json.getValue("pump") instanceof JsonObject) {
      obj.setPump(((JsonObject)json.getValue("pump")).copy());
    }
    if (json.getValue("sink") instanceof JsonObject) {
      obj.setSink(((JsonObject)json.getValue("sink")).copy());
    }
  }

  public static void toJson(PipelineOptions obj, JsonObject json) {
    if (obj.getDeployChannel() != null) {
      json.put("deployChannel", obj.getDeployChannel());
    }
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
    if (obj.getProcessors() != null) {
      json.put("processors", obj.getProcessors());
    }
    if (obj.getPump() != null) {
      json.put("pump", obj.getPump());
    }
    if (obj.getSink() != null) {
      json.put("sink", obj.getSink());
    }
  }
}