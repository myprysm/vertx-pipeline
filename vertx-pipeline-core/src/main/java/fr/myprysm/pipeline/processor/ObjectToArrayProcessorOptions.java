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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;

@DataObject(generateConverter = true)
public class ObjectToArrayProcessorOptions extends ProcessorOptions {
    public static final JsonArray DEFAULT_FIELD = arr();


    private JsonArray fields = DEFAULT_FIELD;

    public ObjectToArrayProcessorOptions() {
        super();
    }

    public ObjectToArrayProcessorOptions(ObjectToArrayProcessorOptions other) {
        super(other);
        fields = other.fields;
    }

    public ObjectToArrayProcessorOptions(ProcessorOptions other) {
        super(other);
    }

    public ObjectToArrayProcessorOptions(JsonObject json) {
        super(json);
        ObjectToArrayProcessorOptionsConverter.fromJson(json, this);
    }

    /**
     * The list of fields to transform as an array / list.
     *
     * @return the list of fields to transform to array.
     */
    public JsonArray getFields() {
        return fields;
    }

    /**
     * The list of fields to transform as an array / list.
     * <p>
     * If one of the fields is not found in the input object, the field is created with an empty array.
     *
     * @param fields the list of fields to transform to array.
     * @return this
     */
    public ObjectToArrayProcessorOptions setFields(JsonArray fields) {
        this.fields = fields;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public ObjectToArrayProcessorOptions setName(String name) {
        return (ObjectToArrayProcessorOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public ObjectToArrayProcessorOptions setType(String type) {
        return (ObjectToArrayProcessorOptions) super.setType(type);
    }

    @Override
    public Integer getInstances() {
        return super.getInstances();
    }

    @Override
    public ObjectToArrayProcessorOptions setInstances(Integer instances) {
        return (ObjectToArrayProcessorOptions) super.setInstances(instances);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        ObjectToArrayProcessorOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectToArrayProcessorOptions)) return false;
        if (!super.equals(o)) return false;
        ObjectToArrayProcessorOptions that = (ObjectToArrayProcessorOptions) o;
        return Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), fields);
    }

    @Override
    public String toString() {
        return "ObjectToArrayProcessorOptions{" +
                "fields=" + fields +
                "} " + super.toString();
    }
}
