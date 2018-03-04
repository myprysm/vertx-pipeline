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

import fr.myprysm.pipeline.util.Flushable;
import fr.myprysm.pipeline.util.Signal;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;

public abstract class FlushableJsonSink<T extends FlushableSinkOptions> extends BaseJsonSink<T> implements Flushable {
    private String controlChannel;

    @Override
    public String controlChannel() {
        return controlChannel;
    }

    @Override
    protected JsonObject preConfiguration(JsonObject config) {
        super.preConfiguration(config);
        controlChannel = exchange().getControlChannel();
        eventBus().consumer(controlChannel, this::handleSignal);
        return config;
    }

    private void handleSignal(Message<Signal> signal) {
        Signal s = signal.body();
        switch (s) {
            case FLUSH:
                this.onSignal(signal.body()).subscribe();
        }
    }


}
