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

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A validation result indicates whether a set of predicates results to a valid or an in valid result.
 * <p>
 * An invalid result can optionally hold a reason.
 */
public interface ValidationResult {
    /**
     * Get a valid result.
     *
     * @return a valid result
     */
    static ValidationResult valid() {
        return ValidationSupport.valid();
    }

    /**
     * Get an invalid result with the provided reason.
     *
     * @param reason the reason
     * @return the invalid result with the reason
     */
    static ValidationResult invalid(String reason) {
        return new Invalid(reason);
    }

    /**
     * Indicates whether the validation result is valid.
     *
     * @return <code>true</code> when the result is valid, <code>false</code> otherwise.
     */
    boolean isValid();

    /**
     * The reason when the result is invalid.
     * <p>
     * The reason should be empty when the result is valid.
     *
     * @return an optional with the reason when the result is invalid
     */
    Optional<String> getReason();

    /**
     * Combines two validations.
     * When the first validation is <code>KO</code> then the <code>other</code> validation is applied and its result is returned.
     *
     * @param other the other validation to apply
     * @return validation result
     */
    default ValidationResult or(Supplier<ValidationResult> other) {
        return this.isValid() ? this : other.get();
    }

    /**
     * Combines two validations.
     * When the first validation is <code>OK</code> then the <code>other</code> validation is applied and its result is returned.
     *
     * @param other the other validation to apply
     * @return validation result
     */
    default ValidationResult and(Supplier<ValidationResult> other) {
        return this.isValid() ? other.get() : this;
    }

    /**
     * Transforms the {@link ValidationResult} into a {@link ValidationException}.
     *
     * @return the exception
     */
    default ValidationException toException() {
        return new ValidationException(this);
    }

    /**
     * Class that represents an invalid result with a reason.
     */
    @ToString
    @EqualsAndHashCode
    final class Invalid implements ValidationResult {

        /**
         * The reason
         */
        private final String reason;

        /**
         * Initialize the invalid result with the reason.
         *
         * @param reason the reason
         */
        Invalid(String reason) {
            this.reason = reason;
        }

        /**
         * Indicates false
         *
         * @return false
         */
        public boolean isValid() {
            return false;
        }

        /**
         * The reason of the error
         *
         * @return the reason
         */
        public Optional<String> getReason() {
            return Optional.of(reason);
        }

    }

    /**
     * Helper class to produce a unique valid result
     */
    final class ValidationSupport {
        /**
         * THE valid result
         */
        private static final ValidationResult valid = new ValidationResult() {
            public boolean isValid() {
                return true;
            }

            public Optional<String> getReason() {
                return Optional.empty();
            }
        };

        private ValidationSupport() {
            //
        }

        /**
         * Get THE valid result
         *
         * @return a valid result
         */
        static ValidationResult valid() {
            return valid;
        }
    }
}
