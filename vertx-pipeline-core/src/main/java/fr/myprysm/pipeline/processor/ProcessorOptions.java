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

import fr.myprysm.pipeline.util.Options;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static io.vertx.core.DeploymentOptions.DEFAULT_INSTANCES;

@DataObject(generateConverter = true)
public class ProcessorOptions implements Options {
    public static final String DEFAULT_NAME = "noop-processor";
    public static final String DEFAULT_TYPE = "fr.myprysm.pipeline.processor.NoOpProcessor";
    public static final Integer DEFAULT_INSTANCES = 1;


    private String name = DEFAULT_NAME;
    private String type = DEFAULT_TYPE;
    private Integer instances = DEFAULT_INSTANCES;

    public ProcessorOptions() {

    }

    public ProcessorOptions(ProcessorOptions other) {
        name = other.name;
        type = other.type;
        instances = other.instances;
    }


    public ProcessorOptions(JsonObject json) {
        ProcessorOptionsConverter.fromJson(json, this);
    }

    /**
     * The name of the processor.
     *
     * @return the name of the processor
     */
    public String getName() {
        return name;
    }

    /**
     * The name of the processor.
     * <p>
     * This is automatically populated when the pipeline configuration is a list.
     * <p>
     * You still can name your processor for any purpose by using a map instead of a list
     * when you describe your pipeline.
     *
     * @param name the name of the processor.
     * @return this
     */
    public ProcessorOptions setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * The type of the processor.
     *
     * @return the type of the processor.
     */
    public String getType() {
        return type;
    }

    /**
     * The type of the processor.
     * <p>
     * This is the fully qualified name of the <code>class</code> that acts as processor.
     *
     * @param type the type of the processor.
     * @return this
     */
    public ProcessorOptions setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * The number of instances to deploy.
     *
     * @return the number of instances to start.
     */
    public Integer getInstances() {
        return instances;
    }


    /**
     * The number of instances to deploy.
     * <p>
     * Must be a positive {@link Integer}.
     *
     * @param instances the number of instances to start
     * @return this
     */
    public ProcessorOptions setInstances(Integer instances) {
        this.instances = instances;
        return this;
    }

    /**
     * Returns this object as a {@link JsonObject}
     *
     * @return this object as json.
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        ProcessorOptionsConverter.toJson(this, json);
        return json;
    }


    @Override
    public String toString() {
        return "ProcessorOptions{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", instances=" + instances +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessorOptions)) return false;
        ProcessorOptions that = (ProcessorOptions) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(instances, that.instances);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, instances);
    }


}
