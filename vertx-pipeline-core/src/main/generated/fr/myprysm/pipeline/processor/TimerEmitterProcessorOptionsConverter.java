
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
 * Converter for {@link fr.myprysm.pipeline.processor.TimerEmitterProcessorOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.processor.TimerEmitterProcessorOptions} original class using Vert.x codegen.
 */
public class TimerEmitterProcessorOptionsConverter {

  public static void fromJson(JsonObject json, TimerEmitterProcessorOptions obj) {
    if (json.getValue("delayTerminate") instanceof Number) {
      obj.setDelayTerminate(((Number)json.getValue("delayTerminate")).longValue());
    }
    if (json.getValue("interval") instanceof Number) {
      obj.setInterval(((Number)json.getValue("interval")).longValue());
    }
    if (json.getValue("signal") instanceof String) {
      obj.setSignal(fr.myprysm.pipeline.util.Signal.valueOf((String)json.getValue("signal")));
    }
    if (json.getValue("unit") instanceof String) {
      obj.setUnit(java.util.concurrent.TimeUnit.valueOf((String)json.getValue("unit")));
    }
  }

  public static void toJson(TimerEmitterProcessorOptions obj, JsonObject json) {
    if (obj.getDelayTerminate() != null) {
      json.put("delayTerminate", obj.getDelayTerminate());
    }
    if (obj.getInterval() != null) {
      json.put("interval", obj.getInterval());
    }
    if (obj.getSignal() != null) {
      json.put("signal", obj.getSignal().name());
    }
    if (obj.getUnit() != null) {
      json.put("unit", obj.getUnit().name());
    }
  }
}