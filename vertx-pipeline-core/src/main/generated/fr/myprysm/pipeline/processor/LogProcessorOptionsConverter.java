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
 * Converter for {@link fr.myprysm.pipeline.processor.LogProcessorOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.processor.LogProcessorOptions} original class using Vert.x codegen.
 */
public class LogProcessorOptionsConverter {

  public static void fromJson(JsonObject json, LogProcessorOptions obj) {
    if (json.getValue("level") instanceof String) {
      obj.setLevel(org.slf4j.event.Level.valueOf((String)json.getValue("level")));
    }
  }

  public static void toJson(LogProcessorOptions obj, JsonObject json) {
    if (obj.getLevel() != null) {
      json.put("level", obj.getLevel().name());
    }
  }
}