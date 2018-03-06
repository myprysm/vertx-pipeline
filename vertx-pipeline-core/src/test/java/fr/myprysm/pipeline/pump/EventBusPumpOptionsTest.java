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

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

public class EventBusPumpOptionsTest {

    @Test
    @DisplayName("Validate EventBusPump options")
    void testEventBusPumpOptionsFeatures() {
        new EventBusPumpOptionsConverter();

        String badStr = "{\"name\": 10, \"type\": 20, \"address\": 1000}";
        String optStr = "{\"name\":\"name\",\"type\":\"type\",\"address\":\"address\"}";

        PumpOptions optPump = new PumpOptions(new JsonObject(optStr));

        EventBusPumpOptions optNull = new EventBusPumpOptions()
                .setAddress(null)
                .setName(null)
                .setType(null);

        EventBusPumpOptions optObj = new EventBusPumpOptions()
                .setName("name")
                .setType("type")
                .setAddress("address");

        JsonObject optJson = new JsonObject(optStr);


        assertThat(new EventBusPumpOptions(new JsonObject(badStr))).isEqualTo(new EventBusPumpOptions());
        assertThat(optNull.toJson()).isEqualTo(obj());
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        assertThat(optObj).isEqualTo(new EventBusPumpOptions(optJson));
        assertThat(optObj).isEqualTo(new EventBusPumpOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new EventBusPumpOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new EventBusPumpOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
        assertThat(optPump)
                .isEqualToComparingOnlyGivenFields(new EventBusPumpOptions(optPump), "name", "type");

    }
}
