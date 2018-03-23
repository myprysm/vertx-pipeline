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

import fr.myprysm.pipeline.util.Alias;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static io.reactivex.Completable.complete;

/**
 * Sink that is able to forward the event to another address on the bus.
 * <p>
 * It can do both <code>publish</code> and <code>send</code> the event
 * to a list of addresses.
 */
@Alias(prefix = "pipeline-core", name = "event-bus-sink")
public class EventBusSink extends BaseJsonSink<EventBusSinkOptions> {

    private List<String> publish;
    private List<String> send;

    @Override
    public void drain(JsonObject item) {
        for (String address : publish) {
            debug("Publishing to {}", address);
            eventBus().publish(address, item);
        }

        for (String address : send) {
            debug("Sending to {}", address);
            eventBus().send(address, item);
        }
    }

    @Override
    protected Completable startVerticle() {
        return complete();
    }

    @Override
    public EventBusSinkOptions readConfiguration(JsonObject config) {
        return new EventBusSinkOptions(config);
    }

    @Override
    public Completable configure(EventBusSinkOptions config) {
        publish = config.getPublish();
        send = config.getSend();
        return complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return EventBusSinkOptionsValidation.validate(config);
    }
}
