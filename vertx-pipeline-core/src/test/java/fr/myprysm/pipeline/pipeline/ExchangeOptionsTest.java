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

package fr.myprysm.pipeline.pipeline;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class ExchangeOptionsTest {

    @Test
    @DisplayName("Validate log processor options")
    void testExchangeOptions() {
        new ExchangeOptionsConverter();

        String badStr = "{\"from\": 10, \"to\": 20, \"controlChannel\": false}";
        String optStr = "{\"from\":\"foo\", \"to\":[\"address1\",\"address2\"], \"controlChannel\":\"custom-channel\"}";

        ExchangeOptions optNull = new ExchangeOptions()
                .setFrom(null)
                .setTo(emptyList())
                .setControlChannel(null);

        ExchangeOptions optObj = new ExchangeOptions()
                .setFrom("foo")
                .setTo(asList("address1", "address2"))
                .setControlChannel("custom-channel");

        JsonObject optJson = new JsonObject(optStr);

        assertThat(new ExchangeOptions(new JsonObject(badStr))).isEqualTo(new ExchangeOptions());
        assertThat(optNull.toJson()).isEqualTo(obj().put("to", arr()));
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        assertThat(optObj).isEqualTo(new ExchangeOptions(optJson));
        assertThat(optObj).isEqualTo(new ExchangeOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new ExchangeOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new ExchangeOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
    }

}