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

import static org.assertj.core.api.Assertions.assertThat;

class ProcessorOptionsTest {

    @Test
    @DisplayName("Validate ProcessorOptions features")
    void testProcessorOptionsFeatures() {
        String optStr = "{\"instances\":1,\"name\": \"name\", \"type\": \"type\"}";
        ProcessorOptions optObj = new ProcessorOptions()
                .setName("name")
                .setType("type");

        JsonObject optJson = new JsonObject(optStr);

        assertThat(optObj).isEqualTo(new ProcessorOptions(optJson));
        assertThat(optObj).isEqualTo(new ProcessorOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new ProcessorOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new ProcessorOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
    }

}
