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

package fr.myprysm.pipeline;

import fr.myprysm.pipeline.validation.ValidationResult;
import io.vertx.core.json.JsonObject;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public interface BaseJsonValidationTest {

    default void isValid(ValidationResult result) {
        assertThat(result.isValid()).isTrue();
    }

    default void isValid(JsonObject config, Function<JsonObject, ValidationResult> validation) {
        isValid(validation.apply(config));
    }

    default void isInvalid(ValidationResult result, String reason) {
        assertThat(result.isValid()).isFalse();
        assertThat(result.getReason()).hasValue(reason);
    }

    default void isInvalid(JsonObject config, Function<JsonObject, ValidationResult> validation, String reason) {
        isInvalid(validation.apply(config), reason);
    }
}
