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

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Helpers to manipulate streams
 */
public interface StreamHelpers {

    /**
     * Stream array elements in reverse-index order
     *
     * @param input the input array
     * @param <T>   the type of object in the array
     * @return the stream
     */
    static <T> Stream<T> streamInReverse(T[] input) {
        return IntStream.range(1, input.length + 1).mapToObj(
                i -> input[input.length - i]);
    }

    /**
     * Stream elements in reverse-index order
     *
     * @param input the input list
     * @param <T>   the type of object in the list
     * @return a stream of the elements in reverse
     */
    static <T> Stream<T> streamInReverse(List<T> input) {
        if (input instanceof LinkedList<?>) {
            return streamInReverse((LinkedList<T>) input);
        }
        return IntStream.range(1, input.size() + 1).mapToObj(i -> input.get(input.size() - 1));
    }

    /**
     * Stream elements in reverse-index order
     *
     * @param input the input linked list
     * @param <T>   the type of object in the list
     * @return a stream of the elements in reverse
     */
    static <T> Stream<T> streamInReverse(LinkedList<T> input) {
        Iterator<T> descendingIterator = input.descendingIterator();
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                descendingIterator, Spliterator.ORDERED), false);
    }
}
