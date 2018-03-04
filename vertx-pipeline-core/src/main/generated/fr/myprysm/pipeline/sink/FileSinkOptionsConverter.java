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

package fr.myprysm.pipeline.sink;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link fr.myprysm.pipeline.sink.FileSinkOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.sink.FileSinkOptions} original class using Vert.x codegen.
 */
public class FileSinkOptionsConverter {

  public static void fromJson(JsonObject json, FileSinkOptions obj) {
    if (json.getValue("file") instanceof String) {
      obj.setFile((String)json.getValue("file"));
    }
    if (json.getValue("format") instanceof String) {
      obj.setFormat(fr.myprysm.pipeline.sink.FileSinkOptions.Format.valueOf((String)json.getValue("format")));
    }
    if (json.getValue("mode") instanceof String) {
      obj.setMode(fr.myprysm.pipeline.sink.FileSinkOptions.Mode.valueOf((String)json.getValue("mode")));
    }
    if (json.getValue("path") instanceof String) {
      obj.setPath((String)json.getValue("path"));
    }
  }

  public static void toJson(FileSinkOptions obj, JsonObject json) {
    if (obj.getFile() != null) {
      json.put("file", obj.getFile());
    }
    if (obj.getFormat() != null) {
      json.put("format", obj.getFormat().name());
    }
    if (obj.getMode() != null) {
      json.put("mode", obj.getMode().name());
    }
    if (obj.getPath() != null) {
      json.put("path", obj.getPath());
    }
  }
}