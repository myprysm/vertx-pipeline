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

package fr.myprysm.pipeline.sink;

import fr.myprysm.pipeline.processor.Processor;
import fr.myprysm.pipeline.pump.Pump;
import fr.myprysm.pipeline.util.Named;
import fr.myprysm.pipeline.util.Stream;

/**
 * A sink is an element able to consume Items from the {@link io.vertx.core.eventbus.EventBus}
 * as far as it is plugged to a {@link Pump} with a pipeline of <code>0..n</code> {@link Processor}.
 * <p>
 * It aims to drains an infinite flow of items from a {@link Pump}.
 */
public interface Sink<I> extends Stream, Named {


    /**
     * Drain one item from the pipeline.
     *
     * @param item the item to drain
     */
    void drain(I item);
}
