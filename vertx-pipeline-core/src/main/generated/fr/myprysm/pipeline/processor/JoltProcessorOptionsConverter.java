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

package fr.myprysm.pipeline.processor;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link fr.myprysm.pipeline.processor.JoltProcessorOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.processor.JoltProcessorOptions} original class using Vert.x codegen.
 */
public class JoltProcessorOptionsConverter {

  public static void fromJson(JsonObject json, JoltProcessorOptions obj) {
    if (json.getValue("format") instanceof String) {
      obj.setFormat(fr.myprysm.pipeline.processor.JoltProcessorOptions.Format.valueOf((String)json.getValue("format")));
    }
    if (json.getValue("path") instanceof String) {
      obj.setPath((String)json.getValue("path"));
    }
    if (json.getValue("specs") instanceof JsonArray) {
      obj.setSpecs(((JsonArray)json.getValue("specs")).copy());
    }
  }

  public static void toJson(JoltProcessorOptions obj, JsonObject json) {
    if (obj.getFormat() != null) {
      json.put("format", obj.getFormat().name());
    }
    if (obj.getPath() != null) {
      json.put("path", obj.getPath());
    }
    if (obj.getSpecs() != null) {
      json.put("specs", obj.getSpecs());
    }
  }
}