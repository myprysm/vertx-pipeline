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
import fr.myprysm.pipeline.util.SignalEmitter;
import io.vertx.core.json.JsonObject;

/**
 * Base {@link Signal} emitter processor.
 * <p>
 * It offers capability to communicate with the {@link fr.myprysm.pipeline.pipeline.PipelineVerticle}
 * as well as the other components in the chain.
 *
 * @param <T> the type of options
 */
public abstract class EmitterJsonProcessor<T extends ProcessorOptions> extends BaseJsonProcessor<T> implements SignalEmitter {
    private String controlChannel;

    @Override
    protected JsonObject preConfiguration(JsonObject json) {
        super.preConfiguration(json);
        controlChannel = exchange().getControlChannel();
        return json;
    }

    @Override
    public String controlChannel() {
        return controlChannel;
    }

    @Override
    public void emitSignal(Signal signal) {
        eventBus().publish(controlChannel(), signal.name());
    }
}
