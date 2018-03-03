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

package fr.myprysm.pipeline.pump;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link fr.myprysm.pipeline.pump.TimerPumpOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.pump.TimerPumpOptions} original class using Vert.x codegen.
 */
public class TimerPumpOptionsConverter {

  public static void fromJson(JsonObject json, TimerPumpOptions obj) {
    if (json.getValue("data") instanceof JsonObject) {
      obj.setData(((JsonObject)json.getValue("data")).copy());
    }
    if (json.getValue("interval") instanceof Number) {
      obj.setInterval(((Number)json.getValue("interval")).longValue());
    }
    if (json.getValue("unit") instanceof String) {
      obj.setUnit(java.util.concurrent.TimeUnit.valueOf((String)json.getValue("unit")));
    }
  }

  public static void toJson(TimerPumpOptions obj, JsonObject json) {
    if (obj.getData() != null) {
      json.put("data", obj.getData());
    }
    if (obj.getInterval() != null) {
      json.put("interval", obj.getInterval());
    }
    if (obj.getUnit() != null) {
      json.put("unit", obj.getUnit().name());
    }
  }
}