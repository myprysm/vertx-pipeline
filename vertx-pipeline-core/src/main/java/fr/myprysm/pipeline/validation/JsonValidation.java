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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

import static fr.myprysm.pipeline.util.JsonHelpers.extractObject;
import static fr.myprysm.pipeline.validation.ValidationResult.invalid;
import static fr.myprysm.pipeline.validation.ValidationResult.valid;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * Base JSON validation class.
 * Provides helpers to test an input {@link JsonObject} and ensure that
 * object structure matches what is expected.
 */
public interface JsonValidation extends Function<JsonObject, ValidationResult> {

    /**
     * Validates that <code>field</code> is not <code>null</code>.
     *
     * @param field the name of the field
     * @return validation result combinator
     */
    static JsonValidation isNotNull(String field) {
        return isNotNull(field, message(field, "is null"));
    }

    /**
     * Validates that <code>field</code> is not <code>null</code>.
     *
     * @param field   the name of the field
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation isNotNull(String field, String message) {
        requireNonNull(field);
        return holds(json -> json.getValue(field) != null, message);
    }

    /**
     * Validates that <code>field</code> is <code>null</code>.
     *
     * @param field the name of the field
     * @return validation result combinator
     */
    static JsonValidation isNull(String field) {
        requireNonNull(field);
        return isNull(field, message(field, "is not null"));
    }

    /**
     * Validates that <code>field</code> is <code>null</code>.
     *
     * @param field   the name of the field
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation isNull(String field, String message) {
        requireNonNull(field);
        return holds(json -> json.getValue(field) == null, message);
    }

    /**
     * Validates that <code>path</code> exists in object.
     *
     * @param path the path
     * @return validation result combinator
     */
    static JsonValidation hasPath(String path) {
        requireNonNull(path);
        return hasPath(path, message(path, "does not exist"));
    }

    /**
     * Validates that <code>path</code> exists in object.
     *
     * @param path    the path
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation hasPath(String path, String message) {
        requireNonNull(path);
        return holds(json -> extractObject(json, path).isPresent(), message);
    }

    /**
     * Validates that <code>field</code> is an <code>object</code>.
     *
     * @param field the name of the field
     * @return validation result combinator
     */
    static JsonValidation isObject(String field) {
        requireNonNull(field);
        return isObject(field, message(field, "is not an object"));
    }

    /**
     * Validates that <code>field</code> is an <code>object</code>.
     *
     * @param field   the name of the field
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation isObject(String field, String message) {
        requireNonNull(field);
        return holds(json -> json.getValue(field) instanceof JsonObject, message);
    }

    /**
     * Validates that <code>field</code> fields are of <code>clazz</code> type.
     * <p>
     * <code>clazz</code> is one of the primitive types or {@link JsonObject} or {@link JsonArray}.
     *
     * @param field the name of the field
     * @param clazz the class to validate
     * @return validation result combinator
     */
    static JsonValidation arrayOf(String field, Class<?> clazz) {
        requireNonNull(field);
        requireNonNull(clazz);
        return arrayOf(field, clazz, message(field, "is not an array of " + clazz.getSimpleName()));
    }

    /**
     * Validates that <code>field</code> fields are of <code>clazz</code> type.
     * <p>
     * <code>clazz</code> is one of the primitive types or {@link JsonObject} or {@link JsonArray}.
     *
     * @param field   the name of the field
     * @param clazz   the class to validate
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation arrayOf(String field, Class<?> clazz, String message) {
        requireNonNull(field);
        requireNonNull(clazz);
        return isArray(field).and(holds(json -> json.getJsonArray(field)
                        .stream()
                        .reduce(TRUE,
                                // Validating entry class against clazz will ensure that
                                // clazz is a valid assignment from a JSON point of view...
                                (valid, entry) -> valid && (entry != null && entry.getClass().isAssignableFrom(clazz)),
                                (r1, r2) -> r1 && r2
                        )
                ,
                message));
    }

    /**
     * Validates that <code>field</code> fields are of <code>clazz</code> type.
     * <p>
     * <code>clazz</code> is one of the primitive types or {@link JsonObject} or {@link JsonArray}.
     *
     * @param field the name of the field
     * @param clazz the class to validate
     * @return validation result combinator
     */
    static JsonValidation mapOf(String field, Class<?> clazz) {
        requireNonNull(field);
        requireNonNull(clazz);
        return mapOf(field, clazz, message(field, "is not a map of " + clazz.getSimpleName()));
    }

    /**
     * Validates that <code>field</code> fields are of <code>clazz</code> type.
     * <p>
     * <code>clazz</code> is one of the primitive types or {@link JsonObject} or {@link JsonArray}.
     *
     * @param field   the name of the field
     * @param clazz   the class to validate
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation mapOf(String field, Class<?> clazz, String message) {
        requireNonNull(field);
        requireNonNull(clazz);
        return isObject(field).and(holds(json -> json.getJsonObject(field)
                        .stream()
                        .reduce(TRUE,
                                // Validating entry class against clazz will ensure that
                                // clazz is a valid assignment from a JSON point of view...
                                (valid, entry) -> valid && (entry.getValue() != null && entry.getValue().getClass().isAssignableFrom(clazz)),
                                (r1, r2) -> r1 && r2
                        )
                ,
                message));
    }

    /**
     * Validates that <code>field</code> is an <code>array</code>.
     *
     * @param field the name of the field
     * @return validation result combinator
     */
    static JsonValidation isArray(String field) {
        requireNonNull(field);
        return isArray(field, message(field, "is not an array"));
    }

    /**
     * Validates that <code>field</code> is an <code>array</code>.
     *
     * @param field   the name of the field
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation isArray(String field, String message) {
        requireNonNull(field);
        return holds(json -> json.getValue(field) instanceof JsonArray, message);
    }

    /**
     * Validates that <code>field</code> is an <code>array</code> of <code>maxSize</code> elements.
     * <p>
     * <code>maxSize</code> must be a positive integer.
     * when <code>maxSize</code> is <code>0</code>, then <code>#isArray(String, int)</code> behaves like {@link #isArray(String)}.
     *
     * @param field   the name of the field
     * @param maxSize the maximum size allowed for the array
     * @return validation result combinator
     */
    static JsonValidation isArray(String field, int maxSize) {
        return isArray(field, maxSize, message(field, "is longer than " + maxSize + " elements"));
    }

    /**
     * Validates that <code>field</code> is an <code>array</code> of <code>maxSize</code> elements.
     * <p>
     * <code>maxSize</code> must be a positive integer.
     * when <code>maxSize</code> is <code>0</code>, then <code>#isArray(String, int)</code> behaves like {@link #isArray(String)}.
     *
     * @param field   the name of the field
     * @param maxSize the maximum size allowed for the array
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation isArray(String field, int maxSize, String message) {
        requireNonNull(field);
        illegalArgument(maxSize < 0);
        return isArray(field)
                .and(holds(
                        json -> maxSize <= 0 || ((JsonArray) json.getValue(field)).size() <= maxSize,
                        message
                ));
    }

    /**
     * Validates that <code>field</code> is a <code>string</code>
     *
     * @param field the name of the field
     * @return validation result combinator
     */
    static JsonValidation isString(String field) {
        return isString(field, message(field, "is not a string"));
    }

    /**
     * Validates that <code>field</code> is a <code>string</code>
     *
     * @param field   the name of the field
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation isString(String field, String message) {
        requireNonNull(field);
        return isNotNull(field)
                .and(holds(json -> String.class.isAssignableFrom(json.getValue(field).getClass()), message));
    }

    /**
     * Validates that <code>field</code> is a <code>string</code> of enum <code>clazz</code>.
     *
     * @param field the name of the field
     * @param clazz the {@link Enum} class
     * @param <T>   the type of the enumeration
     * @return validation result combinator
     */
    static <T extends Enum<T>> JsonValidation isEnum(String field, Class<T> clazz) {
        requireNonNull(field);
        requireNonNull(clazz);
        return isEnum(field, clazz, message(field, "is not part of enum " + clazz.getSimpleName()));
    }

    /**
     * Validates that <code>field</code> is a <code>string</code> of enum <code>clazz</code>.
     *
     * @param field   the name of the field
     * @param clazz   the {@link Enum} class
     * @param message the custom message for validation
     * @param <T>     the type of the enumeration
     * @return validation result combinator
     */
    static <T extends Enum<T>> JsonValidation isEnum(String field, Class<T> clazz, String message) {
        requireNonNull(field);
        requireNonNull(clazz);
        return isString(field)
                .and(holds(json -> Arrays.stream(clazz.getEnumConstants())
                        .map(Enum::name)
                        .anyMatch(json.getValue(field)::equals), message));
    }

    /**
     * Validates that <code>field</code> is a <code>string</code> of enum <code>clazz</code>.
     *
     * @param field  the name of the field
     * @param values the {@link Enum} values
     * @param <T>    the type of the enumeration
     * @return validation result combinator
     */
    static <T extends Enum<T>> JsonValidation isEnum(String field, T... values) {
        requireNonNull(field);
        requireNonNull(values);
        String text = Arrays.stream(values).map(Enum::name).collect(joining(","));
        return isEnum(field, message(field, "is not part of enum " + text), values);
    }

    /**
     * Validates that <code>field</code> is a <code>string</code> of enum <code>clazz</code>.
     *
     * @param field   the name of the field
     * @param values  the {@link Enum} values
     * @param message the custom message for validation
     * @param <T>     the type of the enumeration
     * @return validation result combinator
     */
    @SuppressWarnings("unchecked")
    static <T extends Enum<T>> JsonValidation isEnum(String field, String message, T... values) {
        requireNonNull(field);
        requireNonNull(values);
        return isEnum(field, values[0].getClass())
                .and(holds(json -> Arrays.stream(values)
                        .map(Enum::name)
                        .anyMatch(json.getValue(field)::equals), message));
    }

    /**
     * Validates that <code>field</code> is a <code>boolean</code>.
     *
     * @param field the name of the field
     * @return validation result combinator
     */
    static JsonValidation isBoolean(String field) {
        return isBoolean(field, message(field, "is not a boolean"));
    }

    /**
     * Validates that <code>field</code> is a <code>boolean</code>.
     *
     * @param field   the name of the field
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation isBoolean(String field, String message) {
        requireNonNull(field);
        return isNotNull(field)
                .and(holds(json -> Boolean.class.isAssignableFrom(json.getValue(field).getClass()), message));
    }

    /**
     * Validates that <code>field</code> is a <code>boolean</code> of expected <code>value</code>.
     *
     * @param field the name of the field
     * @param value the value the field must have
     * @return validation result combinator
     */
    static JsonValidation isBoolean(String field, Boolean value) {
        requireNonNull(field);
        requireNonNull(value);
        return isBoolean(field, value, message(field, "is not " + value.toString()));
    }

    /**
     * Validates that <code>field</code> is a <code>boolean</code> of expected <code>value</code>.
     *
     * @param field   the name of the field
     * @param value   the value the field must have
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation isBoolean(String field, Boolean value, String message) {
        requireNonNull(field);
        requireNonNull(value);
        return isBoolean(field)
                .and(holds(json -> value.equals(json.getBoolean(field)), message));
    }


    /**
     * Validates that <code>field</code> is a <code>long</code>.
     *
     * @param field the name of the field
     * @return validation result combinator
     */
    static JsonValidation isLong(String field) {
        requireNonNull(field);
        return isLong(field, message(field, "is not a long"));
    }

    /**
     * Validates that <code>field</code> is a <code>long</code>.
     *
     * @param field   the name of the field
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation isLong(String field, String message) {
        requireNonNull(field);
        return isNotNull(field)
                .and(holds(json -> {
                    Class clazz = json.getValue(field).getClass();
                    return Long.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz);
                }, message));
    }

    /**
     * Validates that <code>field</code> is a <code>long</code> greater than <code>value</code>.
     *
     * @param field the name of the field
     * @param value the minimum value
     * @return validation result combinator
     */
    static JsonValidation gt(String field, Long value) {
        requireNonNull(field);
        requireNonNull(value);
        return gt(field, value, message(field, "is not greater than " + value));
    }

    /**
     * Validates that <code>field</code> is a <code>long</code> greater than <code>value</code>.
     *
     * @param field   the name of the field
     * @param value   the minimum value
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation gt(String field, Long value, String message) {
        requireNonNull(field);
        requireNonNull(value);
        return isLong(field)
                .and(holds(json -> Long.compare(json.getLong(field), value) > 0, message));
    }

    /**
     * Validates that <code>field</code> is a <code>long</code> greater or equal to <code>value</code>.
     *
     * @param field the name of the field
     * @param value the minimum value
     * @return validation result combinator
     */
    static JsonValidation gte(String field, Long value) {
        requireNonNull(field);
        requireNonNull(value);
        return gte(field, value, message(field, "is not greater or equal to " + value));
    }

    /**
     * Validates that <code>field</code> is a <code>long</code> greater or equal to <code>value</code>.
     *
     * @param field   the name of the field
     * @param value   the minimum value
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation gte(String field, Long value, String message) {
        requireNonNull(field);
        requireNonNull(value);
        return isLong(field)
                .and(holds(json -> Long.compare(json.getLong(field), value) >= 0, message));
    }

    /**
     * Validates that <code>field</code> is a <code>long</code> lesser than <code>value</code>.
     *
     * @param field the name of the field
     * @param value the maximum value
     * @return validation result combinator
     */
    static JsonValidation lt(String field, Long value) {
        requireNonNull(field);
        requireNonNull(value);
        return lt(field, value, message(field, "is not lesser than " + value));
    }

    /**
     * Validates that <code>field</code> is a <code>long</code> lesser than <code>value</code>.
     *
     * @param field   the name of the field
     * @param value   the maximum value
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation lt(String field, Long value, String message) {
        requireNonNull(field);
        requireNonNull(value);
        return isLong(field)
                .and(holds(json -> Long.compare(json.getLong(field), value) < 0, message));
    }

    /**
     * Validates that <code>field</code> is a <code>long</code> lesser or equal to <code>value</code>.
     *
     * @param field the name of the field
     * @param value the maximum value
     * @return validation result combinator
     */
    static JsonValidation lte(String field, Long value) {
        requireNonNull(field);
        requireNonNull(value);
        return lte(field, value, message(field, "is not lesser or equal to " + value));
    }

    /**
     * Validates that <code>field</code> is a <code>long</code> lesser or equal to <code>value</code>.
     *
     * @param field   the name of the field
     * @param value   the maximum value
     * @param message the custom message for validation
     * @return validation result combinator
     */
    static JsonValidation lte(String field, Long value, String message) {
        requireNonNull(field);
        requireNonNull(value);
        return isLong(field)
                .and(holds(json -> Long.compare(json.getLong(field), value) <= 0, message));
    }

    static JsonValidation holds(Predicate<JsonObject> p, String message) {
        return json -> p.test(json) ? valid() : invalid(message);
    }

    /**
     * Combines two validations.
     * When the first validation is <code>KO</code> then the <code>other</code> validation is applied and its result is returned.
     *
     * @param other the other validation to apply
     * @return validation result combinator
     */
    default JsonValidation or(JsonValidation other) {
        return json -> {
            final ValidationResult result = this.apply(json);
            return result.isValid() ? result : other.apply(json);
        };
    }

    /**
     * Combines two validations.
     * When the first validation is <code>OK</code> then the <code>other</code> validation is applied and its result is returned.
     *
     * @param other the other validation to apply
     * @return validation result combinator
     */
    default JsonValidation and(JsonValidation other) {
        return json -> {
            final ValidationResult result = this.apply(json);
            return result.isValid() ? other.apply(json) : result;
        };
    }

    /**
     * Returns a preformatted text like "Field '<code>field</code>' <code>message</code>".
     *
     * @param field   the name of the field
     * @param message the message to append to the text
     * @return the message.
     */
    static String message(String field, String message) {
        return "Field '" + field + "' " + message;
    }

    /**
     * Throws an {@link IllegalArgumentException} when the input is <code>true</code>
     *
     * @param inputTest the test input
     */
    static void illegalArgument(boolean inputTest) {
        if (inputTest) {
            throw new IllegalArgumentException();
        }
    }
}
