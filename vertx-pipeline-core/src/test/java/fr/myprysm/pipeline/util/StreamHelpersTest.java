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
import java.util.LinkedList;
import java.util.List;

import static fr.myprysm.pipeline.util.StreamHelpers.streamInReverse;
import static org.assertj.core.api.Assertions.assertThat;

class StreamHelpersTest {

    @Test
    @DisplayName("Streams are reversed.")
    void testThatStreamsAreReversed() {
        String[] array = new String[]{"foo", "bar", "baz"};
        List<String> arrayList = Arrays.asList(array);
        LinkedList<String> linkedList = new LinkedList<>(arrayList);
        List<String> linkedListAsList = linkedList;

        assertThat(streamInReverse(array).findFirst()).hasValue("baz");
        assertThat(streamInReverse(arrayList).findFirst()).hasValue("baz");
        assertThat(streamInReverse(linkedListAsList).findFirst()).hasValue("baz");
        assertThat(streamInReverse(linkedList).findFirst()).hasValue("baz");
    }

}