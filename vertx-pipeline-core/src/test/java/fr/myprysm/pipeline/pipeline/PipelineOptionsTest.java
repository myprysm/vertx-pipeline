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
import static org.assertj.core.api.Assertions.assertThat;

class PipelineOptionsTest {

    @Test
    @DisplayName("Validate Pipeline options")
    void testPipelineOptions() {
        new PipelineOptionsConverter();

        String badStr = "{\"name\": 10, \"type\": 20, \"pump\": 100, \"processors\": \"test\", \"sink\": false}";
        String optStr = "{\"deployChannel\":\"test\",\"name\": \"name\", \"pump\": {}, \"processors\": [{},{}], \"sink\":{}}";

        PipelineOptions optNull = new PipelineOptions()
                .setDeployChannel(null)
                .setName(null)
                .setPump(null)
                .setProcessors(null)
                .setSink(null);

        PipelineOptions optObj = new PipelineOptions()
                .setDeployChannel("test")
                .setName("name")
                .setPump(obj())
                .setProcessors(arr().add(obj()).add(obj()))
                .setSink(obj());

        JsonObject optJson = new JsonObject(optStr);

        assertThat(new PipelineOptions(new JsonObject(badStr))).isEqualTo(new PipelineOptions());
        assertThat(optNull.toJson()).isEqualTo(obj());
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        assertThat(optObj).isEqualTo(new PipelineOptions(optJson));
        assertThat(optObj).isEqualTo(new PipelineOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new PipelineOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new PipelineOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
    }

}