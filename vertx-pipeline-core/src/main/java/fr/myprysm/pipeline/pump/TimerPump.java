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

import java.util.concurrent.TimeUnit;

/**
 * Pump that emits an event at fixed interval.
 * It can embed additional data.
 */
@Alias(prefix = "pipeline-core", name = "timer-pump")
public final class TimerPump extends BaseJsonPump<TimerPumpOptions> {
    private Long interval;
    private TimeUnit unit;
    private JsonObject data;

    @Override
    public ValidationResult validate(JsonObject config) {
        return TimerPumpOptionsValidation.validate(config);
    }

    @Override
    public Flowable<JsonObject> pump() {
        return Flowable.interval(interval, unit)
                .map(tick -> data.copy().put("counter", tick).put("timestamp", System.currentTimeMillis()));
    }

    @Override
    protected Completable startVerticle() {
        return Completable.complete();
    }

    @Override
    public TimerPumpOptions readConfiguration(JsonObject config) {
        return new TimerPumpOptions(config);
    }

    @Override
    public Completable configure(TimerPumpOptions config) {
        interval = config.getInterval();
        unit = config.getUnit();
        data = config.getData();
        return Completable.complete();
    }
}
