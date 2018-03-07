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

import fr.myprysm.pipeline.util.Signal;
import fr.myprysm.pipeline.util.SignalReceiver;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;

import static io.reactivex.Completable.defer;

public abstract class ReceiverJsonSink<T extends SinkOptions> extends BaseJsonSink<T> implements SignalReceiver {
    private String controlChannel;
    private MessageConsumer<String> controlChannelConsumer;

    @Override
    protected JsonObject preConfiguration(JsonObject config) {
        super.preConfiguration(config);
        controlChannel = exchange().getControlChannel();
        controlChannelConsumer = eventBus().consumer(controlChannel, this::handleSignal);
        return config;
    }

    @Override
    public String controlChannel() {
        return controlChannel;
    }

    @Override
    protected Completable preShutdown() {
        return super.preShutdown()
                .andThen(defer(controlChannelConsumer::rxUnregister));
    }

    private void handleSignal(Message<String> message) {
        try {
            this.onSignal(Signal.valueOf(message.body()))
                    .subscribe(this::signalHandledSuccessfully, this::signalHandledWithError);
        } catch (Exception exc) {
            error("Bad signal?!", exc);
        }
    }


    private void signalHandledWithError(Throwable throwable) {
        error("Signal handled with error.", throwable);
    }

    private void signalHandledSuccessfully() {
        debug("Signal handled.");
    }
}

