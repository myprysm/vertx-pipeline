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

/**
 * Represents a component that can buffer some items before
 * writing those to its output
 */
public interface Flushable extends SignalReceiver {

    /**
     * The batch size of the component.
     * Once this size is reached it will flush all its elements
     * before accepting new items.
     *
     * @return the batch size of the component
     */
    Integer batchSize();

    /**
     * Flushes the elements accumulated in the buffer.
     * <p>
     * Indicates its execution status to allow signal emission.
     *
     * @return the status of the flush.
     */
    Completable flush();
}
