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

package fr.myprysm.pipeline.processor;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link fr.myprysm.pipeline.processor.ProcessorOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.processor.ProcessorOptions} original class using Vert.x codegen.
 */
public class ProcessorOptionsConverter {

  public static void fromJson(JsonObject json, ProcessorOptions obj) {
    if (json.getValue("instances") instanceof Number) {
      obj.setInstances(((Number)json.getValue("instances")).intValue());
    }
    if (json.getValue("name") instanceof String) {
      obj.setName((String)json.getValue("name"));
    }
    if (json.getValue("type") instanceof String) {
      obj.setType((String)json.getValue("type"));
    }
  }

  public static void toJson(ProcessorOptions obj, JsonObject json) {
    if (obj.getInstances() != null) {
      json.put("instances", obj.getInstances());
    }
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
    if (obj.getType() != null) {
      json.put("type", obj.getType());
    }
  }
}