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
 * Converter for {@link fr.myprysm.pipeline.sink.EventBusSinkOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.sink.EventBusSinkOptions} original class using Vert.x codegen.
 */
public class EventBusSinkOptionsConverter {

  public static void fromJson(JsonObject json, EventBusSinkOptions obj) {
    if (json.getValue("publish") instanceof JsonArray) {
      java.util.ArrayList<java.lang.String> list = new java.util.ArrayList<>();
      json.getJsonArray("publish").forEach( item -> {
        if (item instanceof String)
          list.add((String)item);
      });
      obj.setPublish(list);
    }
    if (json.getValue("send") instanceof JsonArray) {
      java.util.ArrayList<java.lang.String> list = new java.util.ArrayList<>();
      json.getJsonArray("send").forEach( item -> {
        if (item instanceof String)
          list.add((String)item);
      });
      obj.setSend(list);
    }
  }

  public static void toJson(EventBusSinkOptions obj, JsonObject json) {
    if (obj.getPublish() != null) {
      JsonArray array = new JsonArray();
      obj.getPublish().forEach(item -> array.add(item));
      json.put("publish", array);
    }
    if (obj.getSend() != null) {
      JsonArray array = new JsonArray();
      obj.getSend().forEach(item -> array.add(item));
      json.put("send", array);
    }
  }
}