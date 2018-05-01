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

package fr.myprysm.pipeline.validation;

import fr.myprysm.pipeline.BaseJsonValidationTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static fr.myprysm.pipeline.validation.JsonValidation.*;
import static fr.myprysm.pipeline.validation.ValidationResult.invalid;
import static fr.myprysm.pipeline.validation.ValidationResult.valid;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationTest implements BaseJsonValidationTest {
    private static final JsonObject JSON = obj()
            .put("string", "string")
            .put("env", "ENV:some.environment")
            .put("class", "java.lang.String")
            .put("enum", "SECONDS")
            .put("enumArray", arr().add("SECONDS").add("MINUTES").add("HOURS"))
            .put("true", true)
            .put("false", false)
            .put("long", 10L)
            .put("array", arr().add("item1").add("item2"))
            .put("intArray", arr().add(1).add(2).add(3))
            .put("map", obj()
                    .put("field1", "value1")
                    .put("field2", "value2")
                    .put("field3", "value3")
            )
            .put("object", obj()
                    .put("number", 1)
                    .put("string", "foo")
                    .put("boolean", true)
            )
            .put("intMap", obj()
                    .put("int1", 1)
                    .put("int2", 2)
                    .put("int3", 3)
            );


    @Test
    @DisplayName("Test validation result utility methods")
    void testValidationResultUtils() {
        assertThat(invalid("test")).isNotEqualTo(valid());
        assertThat(invalid("test")).isNotEqualTo(null);
        assertThat(invalid("test")).isNotEqualTo(new Object());
        assertThat(invalid("test")).isNotEqualTo(invalid("other"));
        assertThat(invalid("test")).isEqualTo(invalid("test"));

        assertThat(invalid("test").toString()).isEqualTo("Invalid[reason='test']");

        assertThat(invalid("test").hashCode()).isNotEqualTo(invalid("other").hashCode());
        assertThat(invalid("test").hashCode()).isEqualTo(invalid("test").hashCode());

        assertThat(valid().getReason()).isEmpty();
    }

    @Test
    @DisplayName("Test validation result 'and' & 'or' operators")
    void testValidationResultCombinations() {
        isValid(valid().and(ValidationResult::valid));
        isInvalid(valid().and(() -> invalid("test")), "test");
        isInvalid(invalid("test").and(ValidationResult::valid), "test");
        isInvalid(invalid("left").and(() -> invalid("right")), "left");

        isValid(valid().or(ValidationResult::valid));
        isValid(valid().or(() -> invalid("test")));
        isValid(invalid("test").or(ValidationResult::valid));
        isInvalid(invalid("left").or(() -> invalid("right")), "right");
    }

    @Test
    @DisplayName("Test lazy 'and' validation operator")
    void testLazyAndValidationOperator() {
        AtomicInteger cntAnd = new AtomicInteger();
        Supplier<JsonValidation> supplierAnd = () -> {
            cntAnd.incrementAndGet();
            return isNull("null");
        };

        isValid(JSON, isNull("null").and(supplierAnd));
        assertThat(cntAnd.get()).isEqualTo(1);

        isInvalid(JSON, isNull("string").and(supplierAnd), message("string", "is not null"));
        assertThat(cntAnd.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test lazy 'or' validation operator")
    void testLazyOrValidationOperator() {
        AtomicInteger cntOr = new AtomicInteger();
        Supplier<JsonValidation> supplierOr = () -> {
            cntOr.incrementAndGet();
            return isNull("null");
        };

        isValid(JSON, isNull("null").or(supplierOr));
        assertThat(cntOr.get()).isEqualTo(0);

        isValid(JSON, isNull("string").or(supplierOr));
        assertThat(cntOr.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test env property")
    void testEnvProperty() {
        isValid(JSON, isEnv("env"));
        isInvalid(JSON, isEnv("string"), message("string", "is not an environment property reference"));

    }

    @Test
    @DisplayName("Test null checks")
    void testNullChecks() {
        isValid(JSON, isNull("null"));
        isInvalid(JSON, isNull("string"), message("string", "is not null"));

        isValid(JSON, isNotNull("string"));
        isInvalid(JSON, isNotNull("null"), message("null", "is null"));
    }

    @Test
    @DisplayName("Test objects and maps")
    void testObjectsAndMaps() {
        isValid(JSON, isObject("object"));
        isInvalid(JSON, isObject("string"), message("string", "is not an object"));

        isValid(JSON, mapOf("map", String.class));
        isInvalid(JSON, mapOf("object", String.class), message("object", "is not a map of String"));

        isValid(JSON, mapOf("intMap", Integer.class));
        isInvalid(JSON, mapOf("map", Integer.class), message("map", "is not a map of Integer"));
    }

    @Test
    @DisplayName("Test arrays")
    void testArrays() {
        isValid(JSON, isArray("array"));
        isInvalid(JSON, isArray("string"), message("string", "is not an array"));

        isValid(JSON, isArray("array", 3));
        isInvalid(JSON, isArray("array", 1), message("array", "is longer than 1 elements"));

        isValid(JSON, arrayOf("array", String.class));
        isInvalid(JSON, arrayOf("intArray", String.class), message("intArray", "is not an array of String"));

        isValid(JSON, arrayOf("intArray", Integer.class));
        isInvalid(JSON, arrayOf("array", Integer.class), message("array", "is not an array of Integer"));

        assertThrows(IllegalArgumentException.class, () -> isValid(JSON, isArray("array", -1)));
    }

    @Test
    @DisplayName("Test strings")
    void testStrings() {
        isValid(JSON, isString("string"));
        isInvalid(JSON, isString("long"), message("long", "is not a string"));

        isValid(JSON, isEnum("enum", TimeUnit.class));
        isValid(JSON, isEnum("enumArray", TimeUnit.class));
        isInvalid(JSON, isEnum("string", TimeUnit.class), message("string", "is not part of enum " + TimeUnit.class.getSimpleName()));


        isValid(JSON, isClass("class"));
        isInvalid(JSON, isClass("string"), message("string", "is not a valid java class"));

        isValid(JSON, matches("string", "string"));
        isInvalid(JSON, matches("string", "toto"), message("string", "does not match pattern toto"));
    }

    @Test
    @DisplayName("Test booleans")
    void testBooleans() {
        isValid(JSON, isBoolean("false"));
        isInvalid(JSON, isBoolean("string"), message("string", "is not a boolean"));

        isValid(JSON, isBoolean("true", true).and(isBoolean("false", false)));
        isInvalid(JSON, isBoolean("true", false), message("true", "is not false"));
    }

    @Test
    @DisplayName("Test longs")
    void testLongs() {
        isValid(JSON, isLong("long"));
        isInvalid(JSON, isLong("string"), message("string", "is not a long"));

        isValid(JSON, gt("long", 9L));
        isInvalid(JSON, gt("long", 10L), message("long", "is not greater than 10"));

        isValid(JSON, gte("long", 10L));
        isInvalid(JSON, gte("long", 11L), message("long", "is not greater or equal to 11"));

        isValid(JSON, lt("long", 11L));
        isInvalid(JSON, lt("long", 10L), message("long", "is not lesser than 10"));

        isValid(JSON, lte("long", 10L));
        isInvalid(JSON, lte("long", 9L), message("long", "is not lesser or equal to 9"));
    }
}
