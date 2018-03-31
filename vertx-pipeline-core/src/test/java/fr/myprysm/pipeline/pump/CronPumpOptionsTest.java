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

package fr.myprysm.pipeline.pump;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class CronPumpOptionsTest {
    @Test
    @DisplayName("Validate CronPump options")
    void testCronPumpOptionsFeatures() {
        new CronPumpOptionsConverter();

        String badStr = "{\"name\": 10, \"type\": 20, \"address\": 1000}";
        String optStr = "{\"name\":\"name\",\"type\":\"type\",\"cron\":\"cron\", \"data\": {},\"emitter\":\"fr.myprysm.pipeline.pump.CronEmitter\"}";

        PumpOptions optPump = new PumpOptions(new JsonObject(optStr));

        CronPumpOptions optNull = new CronPumpOptions()
                .setName(null)
                .setType(null)
                .setCron(null)
                .setData(null);

        CronPumpOptions optObj = new CronPumpOptions()
                .setName("name")
                .setType("type")
                .setCron("cron")
                .setData(obj());

        JsonObject optJson = new JsonObject(optStr);


        assertThat(new CronPumpOptions(new JsonObject(badStr))).isEqualTo(new CronPumpOptions());
        assertThat(optNull.toJson()).isEqualTo(obj().put("emitter", "fr.myprysm.pipeline.pump.CronEmitter"));
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        assertThat(optObj).isEqualTo(new CronPumpOptions(optJson));
        assertThat(optObj).isEqualTo(new CronPumpOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new CronPumpOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new CronPumpOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
        assertThat(optPump)
                .isEqualToComparingOnlyGivenFields(new CronPumpOptions(optPump), "name", "type");

    }
}