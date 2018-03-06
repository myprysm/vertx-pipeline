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

package fr.myprysm.pipeline.sink;

import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;

/**
 * Black Hole Sink that drops every incoming event
 * instead of draining it to somewhere else...
 */
final public class BlackholeSink extends BaseJsonSink<SinkOptions> {

    @Override
    protected Completable startVerticle() {
        return Completable.complete();
    }

    @Override
    public void drain(JsonObject item) {
        // Simply discard the item.
        return;
    }

    @Override
    public Completable configure(SinkOptions config) {
        return Completable.complete();
    }
}
