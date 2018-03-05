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

package fr.myprysm.pipeline.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static fr.myprysm.pipeline.validation.JsonValidation.illegalArgument;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * JSON Helpers to manipulate and create items faster.
 */
public interface JsonHelpers {

    String EMPTY_STRING = "";

    /**
     * Writes the provided <code>value</code> in <code>into</code> at <code>path</code>.
     * <p>
     * This function will create the subsequent path if it does not already exists.
     * It will also overwrite any value in this path that is different of a json object.
     * <p>
     * This function is not intended to add a value in an array.
     * <p>
     * Please note that <code>value</code> is <code>nullable</code>.
     *
     * @param json  the object to write the value into
     * @param path  the path to insert / replace the value.
     * @param value the value to write.
     */
    static void writeObject(JsonObject json, String path, Object value) {
        requireNonNull(json);
        illegalArgument(isBlank(path));
        Pair<String, String> pathAndField = toPathAndField(path);
        String p = pathAndField.getLeft();
        String f = pathAndField.getRight();

        if (EMPTY_STRING.equals(p)) {
            json.put(f, value);
            return;
        }

        JsonObject destination = ensurePathExistsAndGet(json, p);
        destination.put(f, value);
    }

    /**
     * Ensures that provided <code>path</code> exists in <code>json</code> object
     * and returns
     * <p>
     * This function will create the subsequent path if it does not already exists.
     * It will also overwrite any value in this path that is different of a json object.
     * <p>
     *
     * @param json the object to write the value into
     * @param path the path to insert / replace the value.
     * @return the object at the position of path.
     */
    static JsonObject ensurePathExistsAndGet(JsonObject json, String path) {
        requireNonNull(json);
        illegalArgument(isBlank(path));
        List<String> sPath = split(path);
        JsonObject field = null;

        if (sPath.size() == 1) {
            field = createOrGet(json, sPath.get(0));
        } else {
            for (String f : sPath) {
                field = createOrGet(field == null ? json : field, f);
            }
        }

        return field;
    }

    /**
     * Ensures that provided <code>field</code> is a {@link JsonObject} in <code>json</code>.
     * <p>
     * If value is <code>null</code> or something else than a {@link JsonObject},
     * then it sets the field to a new blank {@link JsonObject}.
     * <p>
     * Otherwise it returns the {@link JsonObject}.
     *
     * @param json  the input json to get the field
     * @param field the field to get
     * @return the field.
     */
    static JsonObject createOrGet(JsonObject json, String field) {
        Object value = json.getValue(field);
        JsonObject f = obj();
        if (value instanceof JsonObject) {
            f = (JsonObject) value;
        } else {
            json.put(field, f);
        }

        return f;
    }

    /**
     * Extracts the value as a {@link JsonObject} from the provided path.
     *
     * @param json the json to extract the value
     * @param path the path of the field to extract
     * @return an {@link Optional}
     */
    static Optional<JsonObject> extractJsonObject(JsonObject json, String path) {
        requireNonNull(json);
        if (isBlank(path)) return Optional.of(json);
        return Arrays.stream(path.split("\\.")).reduce(Optional.of(json),
                (o, f) -> o.map(e -> JsonObject.class.isAssignableFrom(e.getValue(f, new Object()).getClass()) ? e.getJsonObject(f) : null),
                (o1, o2) -> o1.isPresent() ? o2 : o1);
    }


    /**
     * Extracts the value as a {@link JsonArray} from the provided path.
     *
     * @param json the json to extract the value
     * @param path the path of the field to extract
     * @return an {@link Optional}
     */
    static Optional<JsonArray> extractJsonArray(JsonObject json, String path) {
        requireNonNull(json);
        Pair<String, String> field = toPathAndField(path);
        return extractJsonObject(json, field.getKey()).map(sub -> sub.getJsonArray(field.getValue()));
    }

    /**
     * Extracts the value as a {@link String} from the provided path.
     *
     * @param json the json to extract the value
     * @param path the path of the field to extract
     * @return an {@link Optional}
     */
    static Optional<Object> extractObject(JsonObject json, String path) {
        requireNonNull(json);
        Pair<String, String> field = toPathAndField(path);
        return extractJsonObject(json, field.getKey()).map(sub -> sub.getValue(field.getValue()));
    }

    /**
     * Extracts the value as a {@link String} from the provided path.
     *
     * @param json the json to extract the value
     * @param path the path of the field to extract
     * @return an {@link Optional}
     */
    static Optional<String> extractString(JsonObject json, String path) {
        requireNonNull(json);
        Pair<String, String> field = toPathAndField(path);
        return extractJsonObject(json, field.getKey()).map(sub -> sub.getString(field.getValue()));
    }

    /**
     * Extracts the value as a {@link Integer} from the provided path.
     *
     * @param json the json to extract the value
     * @param path the path of the field to extract
     * @return an {@link Optional}
     */
    static Optional<Integer> extractInt(JsonObject json, String path) {
        requireNonNull(json);
        Pair<String, String> field = toPathAndField(path);
        return extractJsonObject(json, field.getKey()).map(sub -> sub.getInteger(field.getValue()));
    }

    /**
     * Extracts the value as a {@link Long} from the provided path.
     *
     * @param json the json to extract the value
     * @param path the path of the field to extract
     * @return an {@link Optional}
     */
    static Optional<Long> extractLong(JsonObject json, String path) {
        requireNonNull(json);
        Pair<String, String> field = toPathAndField(path);
        return extractJsonObject(json, field.getKey()).map(sub -> sub.getLong(field.getValue()));
    }

    /**
     * Extracts the value as a {@link Float} from the provided path.
     *
     * @param json the json to extract the value
     * @param path the path of the field to extract
     * @return an {@link Optional}
     */
    static Optional<Float> extractFloat(JsonObject json, String path) {
        requireNonNull(json);
        Pair<String, String> field = toPathAndField(path);
        return extractJsonObject(json, field.getKey()).map(sub -> sub.getFloat(field.getValue()));
    }

    /**
     * Extracts the value as a {@link Double} from the provided path.
     *
     * @param json the json to extract the value
     * @param path the path of the field to extract
     * @return an {@link Optional}
     */
    static Optional<Double> extractDouble(JsonObject json, String path) {
        requireNonNull(json);
        Pair<String, String> field = toPathAndField(path);
        return extractJsonObject(json, field.getKey()).map(sub -> sub.getDouble(field.getValue()));
    }

    /**
     * Splits a dotted path into a {@link Pair} of base path and field name
     *
     * @param path the path to split
     * @return a pair with the base path on the left hand and the field name on the right hand
     */
    static Pair<String, String> toPathAndField(String path) {
        illegalArgument(isBlank(path));
        String base = path.lastIndexOf(".") > -1 ? path.substring(0, path.lastIndexOf(".")) : EMPTY_STRING;
        String field = EMPTY_STRING.equals(base) ? path : path.substring(path.lastIndexOf(".") + 1);
        return Pair.of(base, field);
    }

    static List<String> split(String path) {
        illegalArgument(isBlank(path));
        return Arrays.asList(path.split("\\."));
    }


    /**
     * Create a new JSON object
     *
     * @return a new object
     */
    static JsonObject obj() {
        return new JsonObject();
    }

    /**
     * Create a new JSON array
     *
     * @return a new array
     */
    static JsonArray arr() {
        return new JsonArray();
    }
}
