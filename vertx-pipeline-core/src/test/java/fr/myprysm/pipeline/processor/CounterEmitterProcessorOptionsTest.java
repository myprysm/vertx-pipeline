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

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class CounterEmitterProcessorOptionsTest {

    @Test
    @DisplayName("Validate Counter Emitter options")
    void testCounterEmitterProcessorOptions() {
        new CounterEmitterProcessorOptionsConverter();

        String badStr = "{\"name\": 10, \"type\": 20, \"interval\": false, \"signal\": 1}";
        String optStr = "{\"instances\":1,\"name\":\"name\",\"type\":\"type\",\"delayTerminate\":1,\"interval\":100,\"signal\":\"TERMINATE\"}";

        ProcessorOptions optPump = new ProcessorOptions(new JsonObject(optStr));

        CounterEmitterProcessorOptions optNull = new CounterEmitterProcessorOptions()
                .setName(null)
                .setType(null)
                .setInterval(null)
                .setSignal(null);

        CounterEmitterProcessorOptions optObj = new CounterEmitterProcessorOptions()
                .setName("name")
                .setType("type")
                .setInterval(100L)
                .setSignal(Signal.TERMINATE);

        JsonObject optJson = new JsonObject(optStr);

        assertThat(new CounterEmitterProcessorOptions(new JsonObject(badStr))).isEqualTo(new CounterEmitterProcessorOptions());
        assertThat(optNull.toJson()).isEqualTo(obj().put("instances", 1).put("delayTerminate", 1));
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        assertThat(optObj).isEqualTo(new CounterEmitterProcessorOptions(optJson));
        assertThat(optObj).isEqualTo(new CounterEmitterProcessorOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new CounterEmitterProcessorOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new CounterEmitterProcessorOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
        assertThat(optPump)
                .isEqualToComparingOnlyGivenFields(new CounterEmitterProcessorOptions(optPump), "name", "type");
    }

}