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

import fr.myprysm.pipeline.pipeline.ExchangeOptions;
import io.vertx.reactivex.core.eventbus.EventBus;

/**
 * A verticle marked as <code>Stream</code>
 * should provide an interface to Vert.x {@link EventBus}.
 */
public interface Stream {

    /**
     * Returns the addresses of the stream object
     *
     * @return bound addresses
     */
    ExchangeOptions exchange();

    /**
     * Vert.x {@link EventBus}
     *
     * @return the event bus
     */
    EventBus eventBus();
}
