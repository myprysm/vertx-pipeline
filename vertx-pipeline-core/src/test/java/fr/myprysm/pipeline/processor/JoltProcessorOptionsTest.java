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

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class JoltProcessorOptionsTest {

    @Test
    @DisplayName("Validate Merge basic options")
    void testJoltProcessorOptions() {
        new JoltProcessorOptionsConverter();

        String badStr = "{\"name\": 10, \"type\": 20, \"fields\": false}";
        String optStr = "{\"instances\":1,\"name\":\"name\",\"type\":\"type\",\"format\":\"yaml\",\"specs\":[],\"path\":\"some/path.yaml\"}";

        ProcessorOptions optPump = new ProcessorOptions(new JsonObject(optStr));

        JoltProcessorOptions optNull = new JoltProcessorOptions()
                .setName(null)
                .setType(null)
                .setFormat(null)
                .setPath(null)
                .setSpecs(null);

        JoltProcessorOptions optObj = new JoltProcessorOptions()
                .setName("name")
                .setType("type")
                .setInstances(1)
                .setFormat(JoltProcessorOptions.Format.yaml)
                .setPath("some/path.yaml")
                .setSpecs(arr());

        JsonObject optJson = new JsonObject(optStr);

        assertThat(new JoltProcessorOptions(new JsonObject(badStr))).isEqualTo(new JoltProcessorOptions());
        assertThat(optNull.toJson()).isEqualTo(obj().put("instances", 1));
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        assertThat(optObj).isEqualTo(new JoltProcessorOptions(optJson));
        assertThat(optObj).isEqualTo(new JoltProcessorOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new JoltProcessorOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new JoltProcessorOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
        assertThat(optPump)
                .isEqualToComparingOnlyGivenFields(new JoltProcessorOptions(optPump), "name", "type");
    }

}