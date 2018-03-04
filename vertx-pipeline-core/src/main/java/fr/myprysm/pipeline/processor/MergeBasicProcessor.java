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

import fr.myprysm.pipeline.util.JsonHelpers;
import fr.myprysm.pipeline.util.Signal;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Future;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import static fr.myprysm.pipeline.processor.MergeBasicProcessorOptions.*;
import static fr.myprysm.pipeline.util.JsonHelpers.*;
import static fr.myprysm.pipeline.validation.ValidationResult.invalid;
import static fr.myprysm.pipeline.validation.ValidationResult.valid;
import static io.reactivex.Completable.complete;
import static java.util.stream.Collectors.toList;

public class MergeBasicProcessor extends FlushableJsonProcessor<MergeBasicProcessorOptions> {


    private ConcurrentHashMap<Object, JsonObject> map;
    private JsonObject operations;
    private JsonObject onFlush;

    public Single<JsonObject> transform(JsonObject input) {
        // Execute those steps as they come and wait for each to be done
        // before starting to ingest the next event.
        return vertx.rxExecuteBlocking(result -> this.internalTransform(input, result), true);
    }

    private void internalTransform(JsonObject input, Future<JsonObject> result) {
        JsonObject data = input.copy();
        getKeyToObj(data).ifPresent(this::insertItem);

        result.fail(new DiscardableEventException(input));
    }

    private void insertItem(Pair<Object, JsonObject> keyToObj) {
        JsonObject json = keyToObj.getRight();
        JsonObject original = json;

        if (!map.containsKey(keyToObj.getKey())) {
            map.put(keyToObj.getKey(), json);
        } else {
            original = map.get(keyToObj.getKey());
        }

        // No need to compute if this is the first time we see the item.
        if (original != json) {
            mergeArrays(original, json);
        }

        sortArray(original);
    }

    /**
     * Original is the object that will received the merged array.
     *
     * @param original the original object to merge the array
     * @param json     the json to export values into original.
     */
    private void mergeArrays(JsonObject original, JsonObject json) {
        String arrayPath = operations.getString(MERGE_ARRAYS);
        if (arrayPath != null) {
            extractJsonArray(json, arrayPath)
                .ifPresent(newValues -> {
                    JsonArray array = extractJsonArray(original, arrayPath).orElse(arr());
                    List<Object> toAdd = newValues.stream().filter(value -> !array.contains(value)).collect(toList());

                    if (!toAdd.isEmpty()) {
                        array.addAll(new JsonArray(toAdd));
                    }

                    writeObject(json, arrayPath, array);
                });
        }
    }

    private void sortArray(JsonObject json) {
        JsonObject sort = operations.getJsonObject(SORT_ARRAY);
        if (sort != null && canSort(sort)) {
            String path = sort.getString(SORT_PATH);
            String field = sort.getString(SORT_FIELD);
            String order = "DESC".equals(sort.getString(SORT_ORDER)) ? "DESC" : "ASC";
            String type = sort.getString(SORT_TYPE, "string");

            extractJsonArray(json, path)
                .ifPresent(array -> writeObject(json, path,
                    array.stream()
                        .filter(item -> item instanceof JsonObject && ((JsonObject) item).getValue(field) != null)
                        .map(JsonObject.class::cast)
                        .sorted(this.getComparator(field, type, order)).collect(JsonHelpers::arr, JsonArray::add, JsonArray::addAll)
                ));
        }
    }

    /**
     * Constructs a comparator based on the field name, the provided type
     * and the order.
     * <p>
     * Anything else than "DESC" for descending order will result into ascending order.
     *
     * @param field the field to extract
     * @param type  the type of the field, one of long, double, string
     * @param order the ordering
     * @return the comparator
     */
    @SuppressWarnings({"ConstantConditions"})
    private Comparator<JsonObject> getComparator(String field, String type, String order) {
        Comparator<JsonObject> comparator;
        Boolean descending = "DESC".equals(order);
        Pair<String, String> pathAndField = JsonHelpers.toPathAndField(field);
        String p = pathAndField.getLeft();

        ToLongFunction<JsonObject> extractLong = item -> item.getLong(field);
        ToDoubleFunction<JsonObject> extractDouble = item -> item.getDouble(field);
        Function<JsonObject, String> extractString = item -> item.getString(field);

        if (!EMPTY_STRING.equals(p)) {
            extractLong = item -> extractLong(item, field).orElse(null);
            extractDouble = item -> extractDouble(item, field).orElse(null);
            extractString = item -> extractString(item, field).orElse(null);
        }

        if ("long".equals(type)) {
            comparator = Comparator.comparingLong(extractLong);
        } else if ("double".equals(type)) {
            comparator = Comparator.comparingDouble(extractDouble);
        } else {
            comparator = Comparator.comparing(extractString);
        }

        if (descending) {
            comparator = comparator.reversed();
        }

        return Comparator.nullsLast(comparator);
    }

    private boolean canSort(JsonObject json) {
        return json.getValue(SORT_PATH) instanceof String &&
            json.getValue(SORT_FIELD) instanceof String &&
            (json.getValue("order") == null || json.getValue("order") instanceof String);
    }

    private Optional<Pair<Object, JsonObject>> getKeyToObj(JsonObject data) {
        return JsonHelpers.extractObject(data, operations.getString(OBJ_TO_KEY))
            .filter(this::filterAcceptedTypes)
            .map(value -> Pair.of(value, data));
    }

    /**
     * Should accept only Strings and integer numbers.
     *
     * @param value the value to filter
     * @return true if it can be used as a key
     */
    private boolean filterAcceptedTypes(Object value) {
        return value != null && (
            value instanceof String ||
                value instanceof Long ||
                value instanceof Integer
        );
    }

    protected Completable startVerticle() {
        return complete();
    }

    public MergeBasicProcessorOptions readConfiguration(JsonObject config) {
        return new MergeBasicProcessorOptions(config);
    }

    public Completable configure(MergeBasicProcessorOptions config) {
        operations = config.getOperations();
        onFlush = config.getOnFlush();
        map = new ConcurrentHashMap<>(config.getDefaultCapacity().intValue());
        return complete();
    }

    public Completable onSignal(Signal signal) {
        return flush();
    }


    /**
     * Ensure that a mapping key is defined.
     *
     * @param config the configuration
     * @return the validation result
     */
    public ValidationResult validate(JsonObject config) {
        return MergeBasicProcessorOptionsValidation.validate(config).and(() ->
            config.getJsonObject("operations").containsKey(OBJ_TO_KEY) ? valid() : invalid("No mapping set."));
    }

    @Override
    public Integer batchSize() {
        return map.size();
    }

    @Override
    public Completable flush() {
        JsonObject obj = obj();
        JsonArray arr = arr();

        JsonObject sort = onFlush.getJsonObject("sort");
        if (sort != null) {
            String type = sort.getString("type", "string");
            String path = sort.getString("path", "");
            String order = "DESC".equals(sort.getString(SORT_ORDER)) ? "DESC" : "ASC";
            if (!EMPTY_STRING.equals(path)) {
                error("Sorting on {}", type, path, order);
                List<JsonObject> list = new ArrayList<>(map.values());
                list.sort(this.getComparator(path, type, order));
                arr = new JsonArray(list);
            }

            // If sort is not valid, nothing outputs.
        } else {
            error("No sort applied");
            for (JsonObject json : map.values()) {
                arr.add(json);
            }
        }

        obj.put("merged", arr);
        eventBus().send(to(), obj);

        return this.resetMap();
    }

    private Completable resetMap() {
        map = new ConcurrentHashMap<>(configuration().getDefaultCapacity().intValue());
        return complete();
    }
}
