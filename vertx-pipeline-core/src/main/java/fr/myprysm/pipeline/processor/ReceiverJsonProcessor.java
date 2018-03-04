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

import fr.myprysm.pipeline.util.Signal;
import fr.myprysm.pipeline.util.SignalReceiver;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;

import static io.reactivex.Completable.defer;

/**
 * Base {@link Signal} emitter processor.
 * <p>
 * It offers capability to communicate with the {@link fr.myprysm.pipeline.pipeline.PipelineVerticle}
 * as well as the other components in the chain.
 *
 * @param <T> the type of options
 */
public abstract class ReceiverJsonProcessor<T extends ProcessorOptions> extends BaseJsonProcessor<T> implements SignalReceiver {
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
        return super.preShutdown().andThen(defer(controlChannelConsumer::rxUnregister));
    }

    private void handleSignal(Message<String> signal) {
        this.onSignal(Signal.valueOf(signal.body()));
    }
}
