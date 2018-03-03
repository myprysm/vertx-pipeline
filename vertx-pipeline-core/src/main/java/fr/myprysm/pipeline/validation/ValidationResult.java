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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A validation result indicates whether a set of predicates results
 * to a valid or an in valid result.
 * An invalid result can optionally hold a reason.
 */
public interface ValidationResult {
    static ValidationResult valid() {
        return ValidationSupport.valid();
    }

    static ValidationResult invalid(String reason) {
        return new Invalid(reason);
    }

    boolean isValid();

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

    default ValidationException toException() {
        return new ValidationException(this);
    }

    final class Invalid implements ValidationResult {

        private final String reason;

        Invalid(String reason) {
            this.reason = reason;
        }

        public boolean isValid() {
            return false;
        }

        public Optional<String> getReason() {
            return Optional.of(reason);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Invalid invalid = (Invalid) o;
            return Objects.equals(reason, invalid.reason);
        }

        @Override
        public int hashCode() {
            return Objects.hash(reason);
        }

        @Override
        public String toString() {
            return "Invalid[" +
                "reason='" + reason + '\'' +
                ']';
        }
    }

    /**
     * Helper class to produce a unique valid result
     */
    final class ValidationSupport {
        private static final ValidationResult valid = new ValidationResult() {
            public boolean isValid() {
                return true;
            }

            public Optional<String> getReason() {
                return Optional.empty();
            }
        };

        static ValidationResult valid() {
            return valid;
        }
    }
}
