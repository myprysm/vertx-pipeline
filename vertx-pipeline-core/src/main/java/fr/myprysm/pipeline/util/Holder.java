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

import java.util.Objects;

/**
 * Object holder to store references and replace objects stored outside of a lambda in a lambda.
 *
 * @param <T> the type of object to hold.
 */
public class Holder<T> {
    private T value = null;

    /**
     * Builds a new empty holder
     */
    public Holder() {
    }

    /**
     * Builds a new holder with the initial <code>value</code>
     *
     * @param value the initial value
     */
    public Holder(T value) {
        this.value = value;
    }

    /**
     * Get the current value of the holder.
     * <p>
     * Can be <code>null</code>
     *
     * @return the current value
     */
    public T get() {
        return value;
    }

    /**
     * Set the current value of the holder.
     * <p>
     * Can be <code>null</code>
     *
     * @param value the value to set
     * @return this
     */
    public Holder<T> set(T value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Holder)) return false;
        Holder<?> holder = (Holder<?>) o;
        return Objects.equals(value, holder.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Holder{" +
                "value=" + value +
                '}';
    }
}
