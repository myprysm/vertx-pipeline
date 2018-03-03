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

package fr.myprysm.pipeline.sink;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

public class FileSinkOptionsTest {

    @Test
    @DisplayName("Validate File sink options")
    void testFileSinkOptions() {
        new FileSinkOptionsConverter();

        String badStr = "{\"name\": 10, \"type\": 20, \"batchSize\": \"1000\", \"mode\": 1000, \"format\": 100, \"path\": true, \"file\": true}";
        String optStr = "{\"name\":\"name\", \"type\": \"type\", \"batchSize\":100, \"mode\": \"append\", \"format\": \"yaml\", \"path\":\"/test\",\"file\":\"test\"}";

        SinkOptions optPump = new SinkOptions(new JsonObject(optStr));

        FileSinkOptions optNull = new FileSinkOptions()
                .setName(null)
                .setType(null)
                .setBatchSize(null)
                .setMode(null)
                .setFormat(null)
                .setPath(null)
                .setFile(null);

        FileSinkOptions optObj = new FileSinkOptions()
                .setName("name")
                .setType("type")
                .setBatchSize(100)
                .setMode(FileSinkOptions.Mode.append)
                .setFormat(FileSinkOptions.Format.yaml)
                .setPath("/test")
                .setFile("test");

        JsonObject optJson = new JsonObject(optStr);

        assertThat(new FileSinkOptions(new JsonObject(badStr))).isEqualTo(new FileSinkOptions());
        assertThat(optNull.toJson()).isEqualTo(obj());
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        assertThat(optObj).isEqualTo(new FileSinkOptions(optJson));
        assertThat(optObj).isEqualTo(new FileSinkOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new FileSinkOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new FileSinkOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
        assertThat(optPump)
                .isEqualToComparingOnlyGivenFields(new FileSinkOptions(optPump), "name", "type");
    }
}
