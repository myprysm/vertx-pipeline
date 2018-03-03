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

import fr.myprysm.pipeline.validation.ValidationResult;
import io.vertx.core.json.JsonObject;
import org.slf4j.event.Level;

import static fr.myprysm.pipeline.validation.JsonValidation.isEnum;
import static fr.myprysm.pipeline.validation.JsonValidation.isNull;

public interface LogProcessorOptionsValidation {
    static ValidationResult validate(JsonObject config) {
        return isNull("level").or(isEnum("level", Level.class)).apply(config);
    }
}
