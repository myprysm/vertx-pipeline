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

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;

@DataObject(generateConverter = true)
public class DataExtractorProcessorOptions extends ProcessorOptions {
    public static final JsonObject DEFAULT_EXTRACT = obj();

    private JsonObject extract = DEFAULT_EXTRACT;

    public DataExtractorProcessorOptions() {
        super();
    }

    public DataExtractorProcessorOptions(DataExtractorProcessorOptions other) {
        super(other);
        extract = other.extract;
    }

    public DataExtractorProcessorOptions(ProcessorOptions other) {
        super(other);
    }

    public DataExtractorProcessorOptions(JsonObject json) {
        super(json);
        DataExtractorProcessorOptionsConverter.fromJson(json, this);
    }

    /**
     * The list of fields to extract from input json.
     *
     * @return the list of fields to extract.
     */
    public JsonObject getExtract() {
        return extract;
    }

    /**
     * The list of fields to extract from input json.
     * <p>
     * If one of the fields is not found in the input object, the field is ignored.
     * <p>
     * As key, the path to extract a value from input event.
     * As value, the path to put this value to the output event.
     * <p>
     * The complete event can be referenced with the keywork <code>$event</code>
     *
     * @param extract the list of fields to extract
     * @return this
     */
    public DataExtractorProcessorOptions setExtract(JsonObject extract) {
        this.extract = extract;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public DataExtractorProcessorOptions setName(String name) {
        return (DataExtractorProcessorOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public DataExtractorProcessorOptions setType(String type) {
        return (DataExtractorProcessorOptions) super.setType(type);
    }

    @Override
    public Integer getInstances() {
        return super.getInstances();
    }

    @Override
    public DataExtractorProcessorOptions setInstances(Integer instances) {
        return (DataExtractorProcessorOptions) super.setInstances(instances);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        DataExtractorProcessorOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataExtractorProcessorOptions)) return false;
        if (!super.equals(o)) return false;
        DataExtractorProcessorOptions that = (DataExtractorProcessorOptions) o;
        return Objects.equals(extract, that.extract);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), extract);
    }

    @Override
    public String toString() {
        return "DataExtractorProcessorOptions{" +
                "extract=" + extract +
                "} " + super.toString();
    }
}
