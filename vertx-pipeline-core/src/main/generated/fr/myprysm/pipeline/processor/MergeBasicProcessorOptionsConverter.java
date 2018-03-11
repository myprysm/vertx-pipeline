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
 * Converter for {@link fr.myprysm.pipeline.processor.MergeBasicProcessorOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.processor.MergeBasicProcessorOptions} original class using Vert.x codegen.
 */
public class MergeBasicProcessorOptionsConverter {

  public static void fromJson(JsonObject json, MergeBasicProcessorOptions obj) {
    if (json.getValue("defaultCapacity") instanceof Number) {
      obj.setDefaultCapacity(((Number)json.getValue("defaultCapacity")).longValue());
    }
    if (json.getValue("onFlush") instanceof JsonObject) {
      obj.setOnFlush(((JsonObject)json.getValue("onFlush")).copy());
    }
    if (json.getValue("operations") instanceof JsonObject) {
      obj.setOperations(((JsonObject)json.getValue("operations")).copy());
    }
  }

  public static void toJson(MergeBasicProcessorOptions obj, JsonObject json) {
    if (obj.getDefaultCapacity() != null) {
      json.put("defaultCapacity", obj.getDefaultCapacity());
    }
    if (obj.getOnFlush() != null) {
      json.put("onFlush", obj.getOnFlush());
    }
    if (obj.getOperations() != null) {
      json.put("operations", obj.getOperations());
    }
  }
}