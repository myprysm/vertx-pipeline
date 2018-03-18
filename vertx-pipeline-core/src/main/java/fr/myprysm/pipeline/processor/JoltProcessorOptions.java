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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static fr.myprysm.pipeline.util.JsonHelpers.EMPTY_STRING;
import static fr.myprysm.pipeline.util.JsonHelpers.arr;

@DataObject(generateConverter = true)
public class JoltProcessorOptions extends ProcessorOptions {
    private static final JsonArray DEFAULT_SPECS = arr();
    private static final String DEFAULT_PATH = EMPTY_STRING;
    private static final Format DEFAULT_FORMAT = Format.json;

    public enum Format {
        json, yaml
    }

    private JsonArray specs = DEFAULT_SPECS;
    private String path = DEFAULT_PATH;
    private Format format = DEFAULT_FORMAT;

    public JoltProcessorOptions() {
        super();
    }

    public JoltProcessorOptions(JoltProcessorOptions other) {
        super(other);
        specs = other.specs;
        path = other.path;
        format = other.format;
    }

    public JoltProcessorOptions(ProcessorOptions other) {
        super(other);
    }

    public JoltProcessorOptions(JsonObject config) {
        super(config);
        JoltProcessorOptionsConverter.fromJson(config, this);
    }

    /**
     * The JOLT specs as list of transforms
     *
     * @return the specs
     */
    public JsonArray getSpecs() {
        return specs;
    }

    /**
     * The JOLT specs as a list of transforms.
     * <p>
     * Please check JOLT documentation on their <a href="http://bazaarvoice.github.io/jolt/">website</a>.
     * <p>
     * <b>This option is exclusive with <code>path</code> option. If a file is set and valid, <code>specs</code> will be overwritten.</b>
     *
     * @param specs the specs
     * @return this
     */
    public JoltProcessorOptions setSpecs(JsonArray specs) {
        this.specs = specs;
        return this;
    }


    /**
     * The path to a JOLT specs file
     *
     * @return the path to specs file
     */
    public String getPath() {
        return path;
    }

    /**
     * The path to a JOLT specs file.
     * <p>
     * The file can be either <code>JSON</code> or <code>YAML</code>.
     * It must be a list of JOLT transforms. Documentation can be found on their <a href="http://bazaarvoice.github.io/jolt/">website</a>.
     * <p>
     * <b>This option is exclusive with <code>specs</code> option. If a file is set and valid, file specs will overwrite <code>specs</code>.</b>
     *
     * @param path the path to either a <code>JSON</code> or a <code>YAML</code> file.
     * @return this
     */
    public JoltProcessorOptions setPath(String path) {
        this.path = path;
        return this;
    }

    /**
     * The format of the JOLT specs file.
     *
     * @return the format
     */
    public Format getFormat() {
        return format;
    }

    /**
     * The format of the JOLT specs file.
     * <p>
     * Either <code>json</code> or <code>yaml</code>
     *
     * @param format the format
     * @return this
     */
    public JoltProcessorOptions setFormat(Format format) {
        this.format = format;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public JoltProcessorOptions setName(String name) {
        return (JoltProcessorOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public JoltProcessorOptions setType(String type) {
        return (JoltProcessorOptions) super.setType(type);
    }

    @Override
    public Integer getInstances() {
        return super.getInstances();
    }

    @Override
    public JoltProcessorOptions setInstances(Integer instances) {
        return (JoltProcessorOptions) super.setInstances(instances);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        JoltProcessorOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JoltProcessorOptions)) return false;
        if (!super.equals(o)) return false;
        JoltProcessorOptions that = (JoltProcessorOptions) o;
        return Objects.equals(specs, that.specs) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), specs, path);
    }

    @Override
    public String toString() {
        return "JoltProcessorOptions{" +
                "specs=" + specs +
                ", path='" + path + '\'' +
                "} " + super.toString();
    }
}
