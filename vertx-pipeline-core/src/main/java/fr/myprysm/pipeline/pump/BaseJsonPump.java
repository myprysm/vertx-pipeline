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

package fr.myprysm.pipeline.pump;

import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;

/**
 * Base Pump implementation that generates {@link JsonObject} events in the pipeline.
 *
 * @param <T> the options type
 */
public abstract class BaseJsonPump<T extends PumpOptions> extends AbstractPump<JsonObject, T> {
    @Override
    public Completable shutdown() {
        return Completable.complete();
    }
}
