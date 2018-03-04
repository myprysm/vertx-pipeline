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

import java.util.concurrent.TimeUnit;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

public class TimerPumpOptionsTest {

    @Test
    @DisplayName("Validate TimerPump options")
    void testTimerPumpOptionsFeatures() {
        new TimerPumpOptionsConverter();

        String badStr = "{\"name\": 10, \"type\": 20, \"interval\": \"1000\", \"unit\": 1000}";
        String optStr = "{\"name\":\"name\",\"type\":\"type\",\"data\":{},\"interval\":1000,\"unit\":\"HOURS\"}";

        PumpOptions optPump = new PumpOptions(new JsonObject(optStr));

        TimerPumpOptions optNull = new TimerPumpOptions()
                .setInterval(null)
                .setName(null)
                .setType(null)
                .setUnit(null);

        TimerPumpOptions optObj = new TimerPumpOptions()
                .setName("name")
                .setType("type")
                .setInterval(1000L)
                .setUnit(TimeUnit.HOURS);

        JsonObject optJson = new JsonObject(optStr);


        assertThat(new TimerPumpOptions(new JsonObject(badStr))).isEqualTo(new TimerPumpOptions());
        assertThat(optNull.toJson()).isEqualTo(obj().put("data", obj()));
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        assertThat(optObj).isEqualTo(new TimerPumpOptions(optJson));
        assertThat(optObj).isEqualTo(new TimerPumpOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new TimerPumpOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new TimerPumpOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
        assertThat(optPump)
                .isEqualToComparingOnlyGivenFields(new TimerPumpOptions(optPump), "name", "type");

    }
}
