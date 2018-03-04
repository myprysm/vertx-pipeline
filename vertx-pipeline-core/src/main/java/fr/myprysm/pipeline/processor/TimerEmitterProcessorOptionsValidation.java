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
import fr.myprysm.pipeline.validation.JsonValidation;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.vertx.core.json.JsonObject;

import static fr.myprysm.pipeline.pump.TimerPumpOptionsValidation.validInterval;
import static fr.myprysm.pipeline.pump.TimerPumpOptionsValidation.validTimeUnit;
import static fr.myprysm.pipeline.validation.JsonValidation.*;

public interface TimerEmitterProcessorOptionsValidation {

    static ValidationResult validate(JsonObject config) {
        return validTimeUnit()
                .and(validInterval())
                .and(validSignal())
                .and(isNull("delayTerminate").or(gt("delayTerminate", 0L)))
                .apply(config);
    }

    static JsonValidation validSignal() {
        return isNull("signal").or(isEnum("signal", Signal.FLUSH, Signal.TERMINATE));
    }
}
