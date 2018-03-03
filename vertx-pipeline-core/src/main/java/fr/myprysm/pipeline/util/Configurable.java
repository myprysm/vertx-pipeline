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

import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;

/**
 * Indicates the object is configurable.
 * <p>
 * A {@link Configurable} objet provides a {@link #configure(Options)} method.
 * This can be used in component lifecycle to prepare an object before starting it.
 *
 * @param <T> The type of options this {@link Configurable} holds.
 */
public interface Configurable<T extends Options> {

    /**
     * Reads the input JSON to produce an {@link Options} object.
     * <p>
     * This {@link Options} object will be later used to configure the {@link Configurable} object.
     *
     * @param config the configuration to read
     * @return the configuration read from JSON
     */
    T readConfiguration(JsonObject config);

    /**
     * Configures the object with the provided json object.
     *
     * @param config the configuration
     * @return Completable
     */
    Completable configure(T config);
}
