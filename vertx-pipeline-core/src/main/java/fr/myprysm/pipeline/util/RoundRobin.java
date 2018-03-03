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

import java.util.Iterator;
import java.util.List;

/**
 * Round robin iterator.
 *
 * @param <T> The iterated type.
 */
public class RoundRobin<T> implements Iterable<T> {
    private final List<T> list;
    private int size;

    RoundRobin(List<T> list) {
        this.list = list;
        this.size = list.size();
    }

    @Override
    public Iterator<T> iterator() {
        this.size = list.size();
        return new Iterator<T>() {
            private int index;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public T next() {
                index = index % size;
                return list.get(index++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Cannot remove items from list.");
            }
        };
    }

    public static <T> RoundRobin<T> of(List<T> list) {
        return new RoundRobin<>(list);
    }

}
