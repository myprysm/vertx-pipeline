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
import fr.myprysm.pipeline.util.Signal;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static io.reactivex.Completable.complete;

/**
 * Timer emitter that can either send a <code>FLUSH</code> or a <code>TERMINATE</code> signal.
 * <p>
 * In case the timer is configured with <code>TERMINATE</code> it will still send a <code>FLUSH</code>
 * first on the control channel.
 */
@Alias(prefix = "pipeline-core", name = "timer-emitter-processor")
public class TimerEmitterProcessor extends EmitterJsonProcessor<TimerEmitterProcessorOptions> {
    private static final Logger LOG = LoggerFactory.getLogger(TimerEmitterProcessor.class);
    private Long interval;
    private TimeUnit unit;
    private Signal signal;
    private Long delayTerminate;
    private Disposable disposable;

    @Override
    public Single<JsonObject> transform(JsonObject input) {
        return Single.just(input);
    }

    @Override
    protected Completable startVerticle() {
        disposable = Flowable.interval(interval, unit)
                .subscribe(this::handleTimer);
        return complete();
    }

    private void handleTimer(Long tick) {
        emitSignal(Signal.FLUSH);
        if (Signal.TERMINATE == signal) {
            vertx.setTimer(delayTerminate, timerId -> {
                error("Sending terminate {}", tick);
                emitSignal(Signal.TERMINATE);
            });
        }
    }

    @Override
    public TimerEmitterProcessorOptions readConfiguration(JsonObject config) {
        return new TimerEmitterProcessorOptions(config);
    }

    @Override
    public Completable configure(TimerEmitterProcessorOptions config) {
        interval = config.getInterval();
        unit = config.getUnit();
        signal = config.getSignal();
        delayTerminate = config.getDelayTerminate();
        return complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return TimerEmitterProcessorOptionsValidation.validate(config);
    }

    @Override
    protected Logger delegate() {
        return LOG;
    }

    @Override
    public Completable shutdown() {
        disposable.dispose();
        return complete();
    }
}
