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

import io.vertx.core.json.JsonObject;

/**
 * Exception that can throw/return a publisher when
 * it consumes an event but it doesn't want to respond yet.
 */
public class DiscardableEventException extends RuntimeException {

    private JsonObject event;

    public DiscardableEventException(Throwable throwable) {
        super(throwable);
    }

    public DiscardableEventException(Throwable throwable, JsonObject event) {
        this(throwable);
        this.event = event;
    }

    public DiscardableEventException(JsonObject object) {
        event = object;
    }

    public JsonObject getEvent() {
        return event;
    }
}
