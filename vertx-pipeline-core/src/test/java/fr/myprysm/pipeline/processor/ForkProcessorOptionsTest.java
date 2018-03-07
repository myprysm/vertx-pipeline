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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Forks the signal in the chain to new addresses.
 * <p>
 * Those can be new pipelines through {@link fr.myprysm.pipeline.pump.EventBusPump}
 * or any other channel within your application.
 */
class ForkProcessorOptionsTest {

    @Test
    @DisplayName("Validate Data Extractor options")
    void testDataExtractorProcessorOptions() {
        new ForkProcessorOptionsConverter();

        String badStr = "{\"name\": 10, \"type\": 20, \"extract\": false}";
        String optStr = "{\"instances\":1,\"name\":\"name\",\"type\":\"type\",\"publish\":[\"publish-1\",\"publish-2\"],\"send\":[\"send-1\",\"send-2\"]}";

        ProcessorOptions optProc = new ProcessorOptions(new JsonObject(optStr));

        ForkProcessorOptions optNull = new ForkProcessorOptions()
                .setName(null)
                .setType(null)
                .setPublish(null)
                .setSend(null);

        ForkProcessorOptions optObj = new ForkProcessorOptions()
                .setName("name")
                .setType("type")
                .setPublish(asList("publish-1", "publish-2"))
                .setSend(asList("send-1", "send-2"));

        JsonObject optJson = new JsonObject(optStr);

        assertThat(new ForkProcessorOptions(new JsonObject(badStr))).isEqualTo(new ForkProcessorOptions());
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        assertThat(optObj).isEqualTo(new ForkProcessorOptions(optJson));
        assertThat(optObj).isEqualTo(new ForkProcessorOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new ForkProcessorOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new ForkProcessorOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
        assertThat(optProc)
                .isEqualToComparingOnlyGivenFields(new ForkProcessorOptions(optProc), "name", "type");
    }

}