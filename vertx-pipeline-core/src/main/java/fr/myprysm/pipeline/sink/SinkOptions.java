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

package fr.myprysm.pipeline.sink;

import fr.myprysm.pipeline.util.Options;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject(generateConverter = true)
public class SinkOptions implements Options {
    public static final String DEFAULT_NAME = "console-sink";
    public static final String DEFAULT_TYPE = "fr.myprysm.pipeline.processor.NoOpProcessor";


    private String name = DEFAULT_NAME;
    private String type = DEFAULT_TYPE;

    public SinkOptions() {

    }

    public SinkOptions(SinkOptions other) {
        name = other.name;
        type = other.type;
    }


    public SinkOptions(JsonObject json) {
        SinkOptionsConverter.fromJson(json, this);
    }

    /**
     * The name of the sink.
     *
     * @return the name of the sink
     */
    public String getName() {
        return name;
    }

    /**
     * The name of the sink.
     * <p>
     * This is automatically populated when the pipeline configuration is a list.
     * <p>
     * You still can name your sink for any purpose by using a map instead of a list
     * when you describe your pipeline.
     *
     * @param name the name of the sink.
     * @return this
     */
    public SinkOptions setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * The type of the sink.
     *
     * @return the type of the sink.
     */
    public String getType() {
        return type;
    }

    /**
     * The type of the sink.
     * <p>
     * This is the fully qualified name of the <code>class</code> that acts as sink.
     *
     * @param type the type of the sink.
     * @return this
     */
    public SinkOptions setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Returns this object as a {@link JsonObject}
     *
     * @return this object as json.
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        SinkOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public String toString() {
        return "SinkOptions{" +
            "name='" + name + '\'' +
            ", type='" + type + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SinkOptions)) return false;
        SinkOptions that = (SinkOptions) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }


}
