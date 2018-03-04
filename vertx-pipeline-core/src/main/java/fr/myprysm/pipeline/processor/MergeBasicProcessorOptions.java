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

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

@DataObject(generateConverter = true)
public class MergeBasicProcessorOptions extends ProcessorOptions {
    static final String OBJ_TO_KEY = "objToKey";
    static final String MERGE_ARRAYS = "mergeArrays";
    static final String SORT_ARRAY = "sortArray";

    static final String SORT_PATH = "path";
    static final String SORT_FIELD = "field";
    static final String SORT_ORDER = "order";
    static final String SORT_TYPE = "type";

    static final List<String> OPERATIONS = unmodifiableList(asList(OBJ_TO_KEY, MERGE_ARRAYS, SORT_ARRAY));
    static final List<String> ON_FLUSH = unmodifiableList(singletonList("sort"));
    static final Long DEFAULT_CAPACITY = 100L;

    public static final JsonObject DEFAULT_EXTRACT = obj();
    private static final JsonObject DEFAULT_ON_FLUSH = obj();

    private JsonObject operations = DEFAULT_EXTRACT;
    private JsonObject onFlush = DEFAULT_ON_FLUSH;
    private Long defaultCapacity = DEFAULT_CAPACITY;

    public MergeBasicProcessorOptions() {

    }

    public MergeBasicProcessorOptions(MergeBasicProcessorOptions other) {
        super(other);
        operations = other.operations;
        onFlush = other.onFlush;
        defaultCapacity = other.defaultCapacity;
    }

    public MergeBasicProcessorOptions(ProcessorOptions other) {
        super(other);
    }

    public MergeBasicProcessorOptions(JsonObject config) {
        super(config);
        MergeBasicProcessorOptionsConverter.fromJson(config, this);
    }

    /**
     * The list of operations to apply on the accumulating map.
     *
     * @return the list of operations.
     */
    public JsonObject getOperations() {
        return operations;
    }

    /**
     * The list of operations to apply on the accumulating map.
     * <p>
     * Operations available:
     * <ul>
     * <li>objToKey: put the object in the map with the value at path as identifier</li>
     * <li>mergeArrays: merges the arrays at given path on cached object</li>
     * <li>sortArray: sorts the array at field in the corresponding order. Default is ASC</li>
     * </ul>
     * <p>
     * objToKey is required.
     * <p>
     * If the operation is not one of those allowed, it is ignored.
     *
     * @param operations the list of operations
     * @return this
     */
    public MergeBasicProcessorOptions setOperations(JsonObject operations) {
        this.operations = operations;
        return this;
    }

    /**
     * List of on flush signal operations.
     *
     * @return on flush operations
     */
    public JsonObject getOnFlush() {
        return onFlush;
    }

    /**
     * The list of operations to apply on the accumulated map.
     * <p>
     * Operations available:
     * <ul>
     * <li>sort: sorts the objects based on the value at path. Default is ASC</li>
     * </ul>
     * If the operation is not one of those allowed, it is ignored.
     *
     * @param onFlush the list of on flush operations
     * @return this
     */
    public MergeBasicProcessorOptions setOnFlush(JsonObject onFlush) {
        this.onFlush = onFlush;
        return this;
    }

    /**
     * The default capacity of the accumulated map, configured on startup.
     * <p>
     *
     * @return the default capacity
     */
    public Long getDefaultCapacity() {
        return defaultCapacity;
    }

    /**
     * The default capacity of the accumulated map, configured on startup.
     * <p>
     *
     * @param defaultCapacity the default capacity
     * @return this
     */
    public MergeBasicProcessorOptions setDefaultCapacity(Long defaultCapacity) {
        this.defaultCapacity = defaultCapacity;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public MergeBasicProcessorOptions setName(String name) {
        return (MergeBasicProcessorOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public MergeBasicProcessorOptions setType(String type) {
        return (MergeBasicProcessorOptions) super.setType(type);
    }

    @Override
    public Integer getInstances() {
        return super.getInstances();
    }

    @Override
    public MergeBasicProcessorOptions setInstances(Integer instances) {
        return (MergeBasicProcessorOptions) super.setInstances(instances);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        MergeBasicProcessorOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MergeBasicProcessorOptions)) return false;
        if (!super.equals(o)) return false;
        MergeBasicProcessorOptions that = (MergeBasicProcessorOptions) o;
        return Objects.equals(operations, that.operations) &&
            Objects.equals(onFlush, that.onFlush) &&
            Objects.equals(defaultCapacity, that.defaultCapacity);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), operations, onFlush, defaultCapacity);
    }

    @Override
    public String toString() {
        return "MergeBasicProcessorOptions{" +
            "operations=" + operations +
            ", onFlush=" + onFlush +
            ", defaultCapacity=" + defaultCapacity +
            "} " + super.toString();
    }
}
