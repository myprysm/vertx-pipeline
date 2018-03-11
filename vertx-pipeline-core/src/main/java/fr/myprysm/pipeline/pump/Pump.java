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

package fr.myprysm.pipeline.pump;

import fr.myprysm.pipeline.sink.Sink;
import fr.myprysm.pipeline.util.Stream;
import io.reactivex.Flowable;

/**
 * A pump is an element able to produce Items on the {@link io.vertx.core.eventbus.EventBus}
 * as far as its source can produce elements.
 * <p>
 * It aims to pump an infinite flow of items to a {@link Sink}.
 *
 * @param <T> the type of messages this {@link Pump} will pump
 */
public interface Pump<T> extends Stream {

    /**
     * Pumps a flow of items from its source
     *
     * @return the item flow.
     */
    Flowable<T> pump();
}
