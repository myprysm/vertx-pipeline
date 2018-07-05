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

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.impl.ClusterSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DataObject(generateConverter = true)
public class PipelineOptions implements ClusterSerializable {
    public static final String DEFAULT_NAME = "default-pipeline";
    private static final String DEFAULT_DEPLOY_CHANNEL = "default-deploy-channel";

    private String name;
    private JsonObject pump = new JsonObject();
    private JsonArray processors = new JsonArray();
    private JsonObject sink = new JsonObject();
    private String deployChannel = DEFAULT_DEPLOY_CHANNEL;

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

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        PipelineOptionsConverter.toJson(this, json);
        return json;
    }

    /**
     * The name of the pipeline.
     * <p>
     * This is used to build the pipeline components names.
     * Expected to be camel-case ("my-awesome-pipeline").
     *
     * @param name the name of the pipeline
     * @return this
     */
    public PipelineOptions setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * The pump.
     * <p>
     * Mandatory field is <code>type</code>.
     * Name is automatically generated, you can provide your own one though.
     * <p>
     * See vertx-pipeline-core and other modules for more information about specific PumpOptions.
     *
     * @param pump pump options
     * @return this
     */
    public PipelineOptions setPump(JsonObject pump) {
        this.pump = pump;
        return this;
    }

    /**
     * The processor set.
     * Mandatory field for each processor is <code>type</code>.
     * Name is automatically generated, you can provide your own one though.
     * <p>
     * See vertx-pipeline-core and other modules for more information about specific ProcessorOptions.
     *
     * @param processors processor set with options
     * @return this
     */
    public PipelineOptions setProcessors(JsonArray processors) {
        this.processors = processors;
        return this;
    }

    /**
     * The sink.
     * <p>
     * Mandatory field is <code>type</code>.
     * Name is automatically generated, you can provide your own one though.
     * <p>
     * See vertx-pipeline-core and other modules for more information about specific SinkOptions.
     *
     * @param sink sink options
     * @return this
     */
    public PipelineOptions setSink(JsonObject sink) {
        this.sink = sink;
        return this;
    }

    /**
     * The deploy channel.
     * <p>
     * This address is automatically generated when starting the pipeline
     *
     * @param deployChannel the deploy channel address
     * @return this
     */
    public PipelineOptions setDeployChannel(String deployChannel) {
        this.deployChannel = deployChannel;
        return this;
    }

    @Override
    public void writeToBuffer(Buffer buffer) {
        String encoded = toJson().toString();
        byte[] bytes = encoded.getBytes(StandardCharsets.UTF_8);
        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);
    }

    @Override
    public int readFromBuffer(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        int start = pos + 4;
        String encoded = buffer.getString(start, start + length);
        PipelineOptionsConverter.fromJson(new JsonObject(encoded), this);
        return pos + length + 4;
    }
}
