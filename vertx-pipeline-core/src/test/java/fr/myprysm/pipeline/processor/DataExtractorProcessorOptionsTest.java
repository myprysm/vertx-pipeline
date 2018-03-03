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

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class DataExtractorProcessorOptionsTest {

    @Test
    @DisplayName("Validate File sink options")
    void testDataExtractorProcessorOptions() {
        new DataExtractorProcessorOptionsConverter();

        String badStr = "{\"name\": 10, \"type\": 20, \"extract\": false}";
        String optStr = "{\"instances\":1, \"name\":\"name\", \"type\": \"type\", \"extract\":{\"field\":\"value\"}}";

        ProcessorOptions optPump = new ProcessorOptions(new JsonObject(optStr));

        DataExtractorProcessorOptions optNull = new DataExtractorProcessorOptions()
                .setName(null)
                .setType(null)
                .setExtract(null);

        DataExtractorProcessorOptions optObj = new DataExtractorProcessorOptions()
                .setName("name")
                .setType("type")
                .setExtract(obj().put("field", "value"));

        JsonObject optJson = new JsonObject(optStr);

        assertThat(new DataExtractorProcessorOptions(new JsonObject(badStr))).isEqualTo(new DataExtractorProcessorOptions());
        assertThat(optNull.toJson()).isEqualTo(obj().put("instances", 1));
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        assertThat(optObj).isEqualTo(new DataExtractorProcessorOptions(optJson));
        assertThat(optObj).isEqualTo(new DataExtractorProcessorOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new DataExtractorProcessorOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new DataExtractorProcessorOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
        assertThat(optPump)
                .isEqualToComparingOnlyGivenFields(new DataExtractorProcessorOptions(optPump), "name", "type");
    }

}