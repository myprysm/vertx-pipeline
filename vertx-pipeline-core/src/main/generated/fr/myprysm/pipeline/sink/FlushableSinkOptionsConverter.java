/*
 * Copyright 2018 the original author or the original authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.myprysm.pipeline.sink;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link fr.myprysm.pipeline.sink.FlushableSinkOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.sink.FlushableSinkOptions} original class using Vert.x codegen.
 */
public class FlushableSinkOptionsConverter {

  public static void fromJson(JsonObject json, FlushableSinkOptions obj) {
    if (json.getValue("batchSize") instanceof Number) {
      obj.setBatchSize(((Number)json.getValue("batchSize")).intValue());
    }
  }

  public static void toJson(FlushableSinkOptions obj, JsonObject json) {
    if (obj.getBatchSize() != null) {
      json.put("batchSize", obj.getBatchSize());
    }
  }
}