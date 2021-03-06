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

package fr.myprysm.pipeline.pump;

import io.vertx.core.json.JsonObject;
import fr.myprysm.pipeline.validation.JsonValidation;
import fr.myprysm.pipeline.validation.ValidationResult;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;
import static fr.myprysm.pipeline.validation.JsonValidation.*;

public interface TimerPumpOptionsValidation {
    /**
     * Base validator of a {@link TimerPump}
     *
     * @param options the options to validate
     * @return the validation result
     */
    static ValidationResult validate(JsonObject options) {
        requireNonNull(options);
        return validInterval()
                .and(validTimeUnit())
                .and(validData())
                .apply(options);
    }

    static JsonValidation validTimeUnit() {
        return isNull("unit").or(isEnum("unit", TimeUnit.class));
    }

    static JsonValidation validInterval() {
        return isNull("interval").or(gt("interval", 0L));
    }

    static JsonValidation validData() {
        return isNull("data").or(isObject("data"));
    }
}
