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

import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

/**
 * A processor that does nothing.
 * It emits the items as they come.
 */
public final class NoOpProcessor extends BaseJsonProcessor<ProcessorOptions> {

    @Override
    public Single<JsonObject> transform(JsonObject input) {
        return Single.just(input);
    }

    @Override
    protected Completable startVerticle() {
        return Completable.complete();
    }

    @Override
    public ProcessorOptions readConfiguration(JsonObject config) {
        return new ProcessorOptions(config);
    }

    @Override
    public Completable configure(ProcessorOptions config) {
        return Completable.complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return ValidationResult.valid();
    }
}
