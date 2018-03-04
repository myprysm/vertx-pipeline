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

package fr.myprysm.pipeline.pipeline;

import fr.myprysm.pipeline.util.Options;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;

@DataObject(generateConverter = true)
public class PipelineOptions implements Options {
    public static final String DEFAULT_NAME = "default-pipeline";
    private static final String DEFAULT_DEPLOY_CHANNEL = "default-deploy-channel";

    private String name;
    private JsonObject pump = obj();
    private JsonArray processors = arr();
    private JsonObject sink = obj();
    private String deployChannel = DEFAULT_DEPLOY_CHANNEL;

    public PipelineOptions() {
        super();
    }

    public PipelineOptions(PipelineOptions other) {
        name = other.name;
        pump = other.pump;
        processors = other.processors;
        sink = other.sink;
        deployChannel = other.deployChannel;
    }

    public PipelineOptions(JsonObject json) {
        PipelineOptionsConverter.fromJson(json, this);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = obj();
        PipelineOptionsConverter.toJson(this, json);
        return json;
    }

    public String getName() {
        return name;
    }

    public PipelineOptions setName(String name) {
        this.name = name;
        return this;
    }

    public JsonObject getPump() {
        return pump;
    }

    public PipelineOptions setPump(JsonObject pump) {
        this.pump = pump;
        return this;
    }

    public JsonArray getProcessors() {
        return processors;
    }

    public PipelineOptions setProcessors(JsonArray processors) {
        this.processors = processors;
        return this;
    }

    public JsonObject getSink() {
        return sink;
    }

    public PipelineOptions setSink(JsonObject sink) {
        this.sink = sink;
        return this;
    }

    public String getDeployChannel() {
        return deployChannel;
    }

    public PipelineOptions setDeployChannel(String deployChannel) {
        this.deployChannel = deployChannel;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PipelineOptions)) return false;
        PipelineOptions that = (PipelineOptions) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(pump, that.pump) &&
                Objects.equals(processors, that.processors) &&
                Objects.equals(sink, that.sink);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, pump, processors, sink);
    }

    @Override
    public String toString() {
        return "PipelineOptions{" +
                "name='" + name + '\'' +
                ", pump=" + pump +
                ", processors=" + processors +
                ", sink=" + sink +
                '}';
    }
}
