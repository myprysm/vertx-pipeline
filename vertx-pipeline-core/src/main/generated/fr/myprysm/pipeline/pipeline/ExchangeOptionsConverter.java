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
 * Converter for {@link fr.myprysm.pipeline.pipeline.ExchangeOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.pipeline.ExchangeOptions} original class using Vert.x codegen.
 */
public class ExchangeOptionsConverter {

  public static void fromJson(JsonObject json, ExchangeOptions obj) {
    if (json.getValue("controlChannel") instanceof String) {
      obj.setControlChannel((String)json.getValue("controlChannel"));
    }
    if (json.getValue("from") instanceof String) {
      obj.setFrom((String)json.getValue("from"));
    }
    if (json.getValue("to") instanceof JsonArray) {
      java.util.ArrayList<java.lang.String> list = new java.util.ArrayList<>();
      json.getJsonArray("to").forEach( item -> {
        if (item instanceof String)
          list.add((String)item);
      });
      obj.setTo(list);
    }
  }

  public static void toJson(ExchangeOptions obj, JsonObject json) {
    if (obj.getControlChannel() != null) {
      json.put("controlChannel", obj.getControlChannel());
    }
    if (obj.getFrom() != null) {
      json.put("from", obj.getFrom());
    }
    if (obj.getTo() != null) {
      JsonArray array = new JsonArray();
      obj.getTo().forEach(item -> array.add(item));
      json.put("to", array);
    }
  }
}