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

package fr.myprysm.pipeline.processor;

import fr.myprysm.pipeline.util.Signal;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class TimerEmitterProcessorOptionsTest {

    @Test
    @DisplayName("Validate Timer Emitter options")
    void testTimerEmitterProcessorOptions() {
        new TimerEmitterProcessorOptionsConverter();

        String badStr = "{\"name\": 10, \"type\": 20, \"interval\": false, \"unit\":12,\"signal\": 1}";
        String optStr = "{\"instances\":1,\"name\":\"name\",\"type\":\"type\",\"delayTerminate\":1,\"interval\":100,\"signal\":\"TERMINATE\",\"unit\":\"DAYS\"}";

        ProcessorOptions optPump = new ProcessorOptions(new JsonObject(optStr));

        TimerEmitterProcessorOptions optNull = new TimerEmitterProcessorOptions()
                .setName(null)
                .setType(null)
                .setInterval(null)
                .setUnit(null)
                .setSignal(null);

        TimerEmitterProcessorOptions optObj = new TimerEmitterProcessorOptions()
                .setName("name")
                .setType("type")
                .setInterval(100L)
                .setUnit(TimeUnit.DAYS)
                .setSignal(Signal.TERMINATE);

        JsonObject optJson = new JsonObject(optStr);

        assertThat(new TimerEmitterProcessorOptions(new JsonObject(badStr))).isEqualTo(new TimerEmitterProcessorOptions());
        assertThat(optNull.toJson()).isEqualTo(obj().put("instances", 1).put("delayTerminate", 1));
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        assertThat(optObj).isEqualTo(new TimerEmitterProcessorOptions(optJson));
        assertThat(optObj).isEqualTo(new TimerEmitterProcessorOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new TimerEmitterProcessorOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new TimerEmitterProcessorOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
        assertThat(optPump)
                .isEqualToComparingOnlyGivenFields(new TimerEmitterProcessorOptions(optPump), "name", "type");
    }

}