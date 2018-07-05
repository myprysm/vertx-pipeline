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

import fr.myprysm.pipeline.util.Alias;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;

@Alias(prefix = "pipeline-core", name = "event-bus-pump")
public class EventBusPump extends BaseJsonPump<EventBusPumpOptions> {
    private String address;
    private MessageConsumer<JsonObject> consumer;

    @Override
    public Flowable<JsonObject> pump() {
        return consumer.toFlowable().map(this::read);
    }

    private JsonObject read(Message<JsonObject> message) {
        if (message != null && message.body() != null) {
            return message.body();
        }

        error("Got a null event!");
        return obj();
    }

    @Override
    protected Completable startVerticle() {
        consumer = eventBus().consumer(address);
        return Completable.complete();
    }

    @Override
    public EventBusPumpOptions readConfiguration(JsonObject config) {
        return new EventBusPumpOptions(config);
    }

    @Override
    public Completable configure(EventBusPumpOptions config) {
        address = config.getAddress();
        return Completable.complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return EventBusPumpOptionsValidation.validate(config);
    }
}
