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

import fr.myprysm.pipeline.util.Alias;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

import java.util.List;

@Alias(prefix = "pipeline-core", name = "fork-processor")
public class ForkProcessor extends BaseJsonProcessor<ForkProcessorOptions> {

    private List<String> send;
    private List<String> publish;

    @Override
    public Single<JsonObject> transform(JsonObject input) {
        return Single.fromCallable(() -> {
            for (String address : publish) {
                debug("Publishing to {}", address);
                eventBus().publish(address, input);
            }

            for (String address : send) {
                debug("Sending to {}", address);
                eventBus().send(address, input);
            }

            return input;
        });
    }

    @Override
    protected Completable startVerticle() {
        return Completable.complete();
    }

    @Override
    public ForkProcessorOptions readConfiguration(JsonObject config) {
        return new ForkProcessorOptions(config);
    }

    @Override
    public Completable configure(ForkProcessorOptions config) {
        publish = config.getPublish();
        send = config.getSend();
        return Completable.complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return ForkProcessorOptionsValidation.validate(config);
    }
}
