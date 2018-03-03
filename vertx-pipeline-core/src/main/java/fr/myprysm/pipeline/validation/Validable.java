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

package fr.myprysm.pipeline.validation;

import io.vertx.core.json.JsonObject;

import java.util.function.Supplier;

/**
 * Indicates the object is validable.
 * <p>
 * A <code>Validable</code> object provides a {@link #validate(JsonObject)} method that returns a {@link ValidationResult}.
 * This result can be used to indicate whether this object is valid.
 */
public interface Validable {

    /**
     * Supplies the validation of this <code>Validable</code>
     *
     * @param config the configuration
     * @return a supplier to validate this <code>Validable</code> asynchronously
     */
    default Supplier<ValidationResult> supplyValidate(JsonObject config) {
        return () -> this.validate(config);
    }

    /**
     * Validates the configuration.
     *
     * @param config the configuration
     * @return the validation result with a reason.
     */
    ValidationResult validate(JsonObject config);
}
