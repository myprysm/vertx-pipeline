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

import io.vertx.core.eventbus.EventBus;

import java.util.List;

/**
 * Publisher that publish an item on the {@link EventBus}.
 * <p>
 * It must provide a list of recipients so that scaling and load balancing can be inherant
 * to the system.
 * <p>
 * It must also provide the next address that will be used to publish message
 *
 * @param <T> the type of the item
 */
public interface Publisher<T> {

    /**
     * Provides the address to publish an item
     *
     * @return the address to publish.
     */
    String to();

    /**
     * Address list of this publisher.
     *
     * @return the address list of this publisher.
     */
    List<String> recipients();

    /**
     * Publish an item on the pipeline.
     *
     * @param item the item to publish.
     */
    void publish(T item);
}
