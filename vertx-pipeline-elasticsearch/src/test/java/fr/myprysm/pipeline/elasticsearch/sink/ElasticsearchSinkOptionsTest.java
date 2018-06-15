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

package fr.myprysm.pipeline.elasticsearch.sink;

import fr.myprysm.pipeline.sink.SinkOptions;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class ElasticsearchSinkOptionsTest {

    @Test
    @DisplayName("Validate Event Bus Sink options")
    void testEventBusSinkOptionsOptions() {
        new ElasticsearchSinkOptionsConverter();

        String badStr = "{\"name\": 10, \"type\": 20, \"extract\": false}";
        String optStr = "{\"name\":\"name\",\"type\":\"type\",\"bulk\":false,\"bulkSize\":100,\"generateId\":\"none\",\"indexName\": \"indexName\",\"indexType\": \"indexType\",\"hosts\":[{\"hostname\":\"localhost\",\"port\":9200}]}";

        SinkOptions optSink = new SinkOptions(new JsonObject(optStr));

        ElasticsearchSinkOptions optNull = new ElasticsearchSinkOptions()
                .setName(null)
                .setType(null)
                .setBulk(null)
                .setBulkSize(null)
                .setHosts(null)
                .setIndexName(null)
                .setIndexType(null);

        ElasticsearchSinkOptions optObj = new ElasticsearchSinkOptions()
                .setName("name")
                .setType("type")
                .setBulk(false)
                .setBulkSize(100)
                .setHosts(arr().add(obj().put("hostname", "localhost").put("port", 9200)))
                .setIndexName("indexName")
                .setIndexType("indexType");

        JsonObject optJson = new JsonObject(optStr);

        assertThat(new ElasticsearchSinkOptions(new JsonObject(badStr))).isEqualTo(new ElasticsearchSinkOptions());
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        assertThat(optObj).isEqualTo(new ElasticsearchSinkOptions(optJson));
        assertThat(optObj).isEqualTo(new ElasticsearchSinkOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new ElasticsearchSinkOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new ElasticsearchSinkOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
        assertThat(optSink)
                .isEqualToComparingOnlyGivenFields(new ElasticsearchSinkOptions(optSink), "name", "type");
    }


}