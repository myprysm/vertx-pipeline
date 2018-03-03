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

package fr.myprysm.pipeline.processor;

import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import fr.myprysm.pipeline.pump.Pump;
import fr.myprysm.pipeline.sink.Sink;
import fr.myprysm.pipeline.validation.ValidationResult;

/**
 * Base of a pipeline processor.
 * <p>
 * A {@link Processor} is a verticle able to read data from another {@link Processor} or from a {@link Pump}
 * and to send data to another cluster of {@link Processor} or a {@link Sink}.
 * <p>
 * It handles {@link JsonObject} <b>only</b> to facilitate the data processing as JSON manipulation is quite trivial.
 * <p>
 * Data is emitted to recipient(s) in a round robin fashion.
 */
public abstract class BaseJsonProcessor<O extends ProcessorOptions> extends AbstractProcessor<JsonObject, JsonObject, O> {
    @Override
    public Completable shutdown() {
        return Completable.complete();
    }

}
