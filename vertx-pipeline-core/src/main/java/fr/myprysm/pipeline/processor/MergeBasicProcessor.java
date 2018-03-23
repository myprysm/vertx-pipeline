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

package fr.myprysm.pipeline.processor;

import fr.myprysm.pipeline.util.Alias;
import fr.myprysm.pipeline.util.JsonHelpers;
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

/**
 * Merge processor that combines incoming events by mapping them from a unique key until it receives a <code>FLUSH</code> signal.
 * <p>
 * It provides the capability to merge some array on the incoming message to another one already stored in the accumulator.
 * It provides also the capability to sort this array based on some field of the objects stored in this array.
 * This field <b>MUST</b> a <code>string</code> or a <code>number</code>.
 * If this field is not set on one of the items, <b>the item is discarded</b>.
 * <p>
 * When flushing accumulated data, it is also possible to sort the list of accumulated events based on a specific field.
 */
@Accumulator
@Alias(prefix = "pipeline-core", name = "merge-basic-processor")
public class MergeBasicProcessor extends FlushableJsonProcessor<MergeBasicProcessorOptions> {


    private ConcurrentHashMap<Object, JsonObject> map;
    private JsonObject operations;
    private JsonObject onFlush;

    @Override
    public Single<JsonObject> transform(JsonObject input) {
        // Execute those steps as they come and wait for each to be done
        // before starting to ingest the next event.
        return vertx.rxExecuteBlocking(result -> this.internalTransform(input, result), true);
    }

    /**
     * Processes the incoming event when it is possible.
     * <p>
     * The event is discarded.
     *
     * @param input  the input event
     * @param result the result of the async processing.
     */
    private void internalTransform(JsonObject input, Future<JsonObject> result) {
        JsonObject data = input.copy();
        getKeyToObj(data).ifPresent(this::insertItem);

        result.fail(new DiscardableEventException(input));
    }

    /**
     * Inserts the item if it does not already exists.
     * <p>
     * When it has not been inserted then it will try to perform the merge arrays operation.
     * <p>
     * Once object is inserted or merge is done, then it will try to apply the sort.
     *
     * @param keyToObj the paire with key on the left and object on the right.
     */
    private void insertItem(Pair<Object, JsonObject> keyToObj) {
        JsonObject json = keyToObj.getRight();
        JsonObject original = json;

        if (!map.containsKey(keyToObj.getKey())) {
            debug("No item on key {}", keyToObj.getKey());
            map.put(keyToObj.getKey(), json);
        } else {
            debug("Already an item on key {}", keyToObj.getKey());
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
     * <p>
     * It filters new incoming values that are already contained in the original array
     * before adding remaining values.
     *
     * @param original the original object to merge the array
     * @param json     the json to export values into original.
     */
    private void mergeArrays(JsonObject original, JsonObject json) {
        String arrayPath = operations.getString(MERGE_ARRAYS);
        if (arrayPath != null) {
            extractJsonArray(json, arrayPath)
                    .ifPresent(newValues -> {
                        debug("Merging array at path {}", arrayPath);
                        JsonArray array = extractJsonArray(original, arrayPath).orElse(arr());
                        List<Object> toAdd = newValues.stream().filter(value -> !array.contains(value)).collect(toList());

                        if (!toAdd.isEmpty()) {
                            array.addAll(new JsonArray(toAdd));
                        }

                        writeObject(json, arrayPath, array);
                    });
        }
    }

    /**
     * Sorts the array configured when accumulating values.
     * <p>
     * If the provided path does not exists, simply does nothing.
     * Defaults to "ASC" for order, "string" for comparison.
     *
     * @param json the json object to sort array in.
     */
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

    /**
     * Validates that the sort configuration is valid for sortArray operation.
     *
     * @param json the configuration
     * @return <code>true</code> when configuration is considered as valid for sort.
     */
    private boolean canSort(JsonObject json) {
        return json.getValue(SORT_PATH) instanceof String &&
                json.getValue(SORT_FIELD) instanceof String &&
                (json.getValue("order") == null || json.getValue("order") instanceof String);
    }

    /**
     * Extracts the key in data object, asserts that it is an acceptable type for a key
     * and returns a pair with the key on the left hand and data on the right hand
     *
     * @param data the event
     * @return an optional pair with key on the left hand and data on the right hand.
     */
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
        return value != null &&
                (value instanceof String ||
                        value instanceof Long ||
                        value instanceof Integer);
    }

    @Override
    protected Completable startVerticle() {
        return complete();
    }

    @Override
    public MergeBasicProcessorOptions readConfiguration(JsonObject config) {
        return new MergeBasicProcessorOptions(config);
    }


    @Override
    public Completable configure(MergeBasicProcessorOptions config) {
        operations = config.getOperations();
        onFlush = config.getOnFlush();
        map = new ConcurrentHashMap<>(config.getDefaultCapacity().intValue());
        return complete();
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
        return Optional.ofNullable(map).map(ConcurrentHashMap::size).orElse(0);
    }

    @Override
    public Completable flush() {
        JsonObject obj = obj();
        JsonArray arr = arr();

        // Build output anyway, thus even if sort does not apply, then the next component will still receive data.
        for (JsonObject json : map.values()) {
            arr.add(json);
        }

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
        }

        obj.put("merged", arr);
        eventBus().send(to(), obj);

        return this.resetMap();
    }

    /**
     * Resets the map.
     * <p>
     * Triggered usually as last operation of a flush
     *
     * @return complete when the map has been reset.
     */
    private Completable resetMap() {
        map = new ConcurrentHashMap<>(configuration().getDefaultCapacity().intValue());
        return complete();
    }
}
