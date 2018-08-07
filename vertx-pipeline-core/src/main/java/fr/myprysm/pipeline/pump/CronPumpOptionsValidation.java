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

import fr.myprysm.pipeline.validation.JsonValidation;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.vertx.core.json.JsonObject;

import java.text.ParseException;

import static fr.myprysm.pipeline.util.ClasspathHelpers.getCronEmitterClassNames;
import static fr.myprysm.pipeline.validation.JsonValidation.holds;
import static fr.myprysm.pipeline.validation.JsonValidation.isNull;
import static fr.myprysm.pipeline.validation.JsonValidation.isString;
import static org.quartz.CronScheduleBuilder.cronScheduleNonvalidatedExpression;

/**
 * Validates <code>CronPumpOptions</code>.
 */
public interface CronPumpOptionsValidation {

    /**
     * Validate the configuration for <code>CronPumpOptions</code>.
     *
     * @param config the configuration
     * @return the validation result
     */
    static ValidationResult validate(JsonObject config) {
        return isCronValid()
                .and(isEmitterValid())
                .apply(config);
    }

    /**
     * Validates that the emitter is a CronEmitter.
     *
     * @return the validation chain.
     */
    static JsonValidation isEmitterValid() {
        return isNull("emitter").or(isString("emitter")
                .and(holds(json -> getCronEmitterClassNames().contains(json.getString("emitter")), "The class is not a kind of CronEmitter")));
    }

    /**
     * Validates the cron expression.
     *
     * @return the validation chain
     */
    static JsonValidation isCronValid() {
        return isString("cron").and(holds(json -> {
            try {
                cronScheduleNonvalidatedExpression(json.getString("cron"));
            } catch (ParseException e) {
                return false;
            }
            return true;
        }, "Cron expression is not valid"));
    }
}
