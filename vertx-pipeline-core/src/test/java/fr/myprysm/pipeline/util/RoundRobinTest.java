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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class RoundRobinTest {

    @Test
    @DisplayName("Test Round Robin")
    void testRoundRobin() {
        RoundRobin<String> rb = RoundRobin.of(Arrays.asList("foo", "bar", "baz"));
        Iterator<String> it = rb.iterator();

        assertThat(it.hasNext()).isTrue();
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(it::remove);
        assertThat(it.next()).isEqualTo("foo");
        assertThat(it.next()).isEqualTo("bar");
        assertThat(it.next()).isEqualTo("baz");
        assertThat(it.next()).isEqualTo("foo");
        assertThat(it.hasNext()).isTrue();
    }

}