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

package fr.myprysm.pipeline.util;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.*;

class JsonHelpersTest {

    private JsonObject json;

    @BeforeEach
    void setUp() {
        json = obj();
    }

    @Test
    @DisplayName("Write operations work")
    void testWriteOperations() {
        JsonObject obj = obj();
        JsonHelpers.writeObject(json, "test.path", "test");
        assertThat(json.getJsonObject("test").getString("path")).isEqualTo("test");
        JsonHelpers.writeObject(json, "a.long.path.to.test.an.int", 1_000_000);
        assertThat(JsonHelpers.extractInt(json, "a.long.path.to.test.an.int")).hasValue(1_000_000);
        assertThat(obj == JsonHelpers.createOrGet(json, "test")).isFalse();

        JsonHelpers.writeObject(json, "obj", obj);
        assertThat(obj == JsonHelpers.createOrGet(json, "obj")).isTrue();

    }

}