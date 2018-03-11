
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
 * Converter for {@link fr.myprysm.pipeline.processor.DataExtractorProcessorOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.processor.DataExtractorProcessorOptions} original class using Vert.x codegen.
 */
public class DataExtractorProcessorOptionsConverter {

  public static void fromJson(JsonObject json, DataExtractorProcessorOptions obj) {
    if (json.getValue("extract") instanceof JsonObject) {
      obj.setExtract(((JsonObject)json.getValue("extract")).copy());
    }
  }

  public static void toJson(DataExtractorProcessorOptions obj, JsonObject json) {
    if (obj.getExtract() != null) {
      json.put("extract", obj.getExtract());
    }
  }
}