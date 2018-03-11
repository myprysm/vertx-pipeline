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

package fr.myprysm.pipeline.processor;

import fr.myprysm.pipeline.pump.Pump;
import fr.myprysm.pipeline.sink.Sink;
import fr.myprysm.pipeline.util.Stream;
import io.reactivex.Single;

/**
 * A Processor is part of a pipeline configured from a {@link Pump} to a {@link Sink}.
 * <p>
 * It receives items directly from a {@link Pump} or from another {@link Processor}.
 * Each incoming item receives a transformation before being returned to the pipeline.
 * The output can be sent directly to a {@link Sink} to another {@link Processor}.
 *
 * @param <I> The type of input items
 * @param <O> The type of output items
 */
public interface Processor<I, O> extends Stream {

    /**
     * Transforms the input item into a new item.
     * This function is basically a mapper that takes it's input, does some processing on it,
     * and finally produces a new item as output.
     *
     * @param input the item to transform
     * @return the transformed item.
     */
    Single<O> transform(I input);

}
