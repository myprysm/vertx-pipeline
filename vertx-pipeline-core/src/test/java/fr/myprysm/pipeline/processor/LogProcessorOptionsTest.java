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

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class LogProcessorOptionsTest {

    @Test
    @DisplayName("Validate log processor options")
    void testLogProcessorOptions() {
        new LogProcessorOptionsConverter();

        String badStr = "{\"name\": 10, \"type\": 20, \"level\": false}";
        String optStr = "{\"instances\":1, \"name\":\"name\", \"type\": \"type\", \"level\":\"ERROR\"}";

        ProcessorOptions optPump = new ProcessorOptions(new JsonObject(optStr));

        LogProcessorOptions optNull = new LogProcessorOptions()
                .setName(null)
                .setType(null)
                .setLevel(null);

        LogProcessorOptions optObj = new LogProcessorOptions()
                .setName("name")
                .setType("type")
                .setLevel(Level.ERROR);

        JsonObject optJson = new JsonObject(optStr);

        assertThat(new LogProcessorOptions(new JsonObject(badStr))).isEqualTo(new LogProcessorOptions());
        assertThat(optNull.toJson()).isEqualTo(obj().put("instances", 1));
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        assertThat(optObj).isEqualTo(new LogProcessorOptions(optJson));
        assertThat(optObj).isEqualTo(new LogProcessorOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new LogProcessorOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new LogProcessorOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
        assertThat(optPump)
                .isEqualToComparingOnlyGivenFields(new LogProcessorOptions(optPump), "name", "type");
    }
}