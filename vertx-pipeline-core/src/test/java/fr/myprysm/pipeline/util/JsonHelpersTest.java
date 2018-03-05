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

import static fr.myprysm.pipeline.util.JsonHelpers.*;
import static org.assertj.core.api.Assertions.assertThat;

class JsonHelpersTest {

    private JsonObject json;

    @BeforeEach
    void setUp() {
        json = obj();
    }

    @Test
    @DisplayName("JSON write operations")
    void testWriteOperations() {
        JsonObject obj = obj();
        writeObject(json, "test.path", "test");
        assertThat(json.getJsonObject("test").getString("path")).isEqualTo("test");

        writeObject(json, "a.long.path.to.test.an.int", 1_000_000);
        assertThat(extractInt(json, "a.long.path.to.test.an.int")).hasValue(1_000_000);
        assertThat(obj == JsonHelpers.createOrGet(json, "test")).isFalse();

        writeObject(json, "obj", obj);
        assertThat(obj == JsonHelpers.createOrGet(json, "obj")).isTrue();

    }

    @Test
    @DisplayName("JSON extractors")
    @SuppressWarnings("unchecked")
    void testExtractors() {
        JsonObject obj = obj();
        JsonObject nb = obj();

        obj.put("numbers", nb);
        nb.put("int", 1).put("long", 10L).put("float", 2.5F).put("double", 25.75D);
        obj.put("deeper", obj().put("deeper", obj().put("numbers", nb.copy())));
        writeObject(obj, "nested.array", arr().add("item"));

        assertThat(extractInt(obj, "numbers.int")).hasValue(1);
        assertThat(extractLong(obj, "deeper.deeper.numbers.long")).hasValue(10L);
        assertThat(extractDouble(obj, "numbers.double")).hasValue(25.75D);
        assertThat(extractFloat(obj, "deeper.deeper.numbers.float")).hasValue(2.5F);
        assertThat(extractJsonArray(obj, "nested.array")).hasValue(arr().add("item"));

    }

}