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

package fr.myprysm.pipeline.pump;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PumpOptionsTest {
    @Test
    @DisplayName("Validate PumpOptions features")
    void testPumpOptionsFeatures() {
        String optStr = "{\"name\": \"name\", \"type\": \"type\"}";
        PumpOptions optObj = new PumpOptions()
            .setName("name")
            .setType("type");

        JsonObject optJson = new JsonObject(optStr);

        assertThat(optObj).isEqualTo(new PumpOptions(optJson));
        assertThat(optObj).isEqualTo(new PumpOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new PumpOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new PumpOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
    }
}
