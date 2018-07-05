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

package fr.myprysm.pipeline.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class HolderTest {

    private Holder<?> holder;

    @BeforeEach
    void setUp() {
        holder = new Holder<>().set(obj());
    }

    @Test
    @DisplayName("Test holder utilities")
    void testHolderUtilities() {
        Holder<JsonObject> equal = new Holder<>(obj());
        Holder<JsonArray> notEqual = new Holder<>(arr());
        assertThat(holder).isEqualTo(equal);
        assertThat(holder).isNotEqualTo(null);
        assertThat(holder).isNotEqualTo(notEqual);
        assertThat(holder.hashCode()).isEqualTo(equal.hashCode());
        assertThat(holder.hashCode()).isNotEqualTo(notEqual.hashCode());

        assertThat(holder.toString()).isEqualTo(equal.toString());
        assertThat(holder.toString()).isNotEqualTo(notEqual.toString());
    }
}