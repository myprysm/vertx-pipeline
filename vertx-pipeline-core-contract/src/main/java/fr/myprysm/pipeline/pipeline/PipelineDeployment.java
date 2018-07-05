package fr.myprysm.pipeline.pipeline;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.impl.ClusterSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;

/**
 * Response provided when a pipeline is successfully deployed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@DataObject(generateConverter = true)
public class PipelineDeployment implements ClusterSerializable {

    private String id;
    private String node;
    private String name;
    private String controlChannel;

    public PipelineDeployment(PipelineDeployment other) {
        id = other.id;
        node = other.node;
        name = other.name;
        controlChannel = other.controlChannel;
    }

    public PipelineDeployment(JsonObject json) {
        PipelineDeploymentConverter.fromJson(json, this);
    }


    /**
     * The pipeline identifier.
     * <p>
     * This is basically the deployment ID for the <code>PipelineVerticle</code>.
     * <p>
     * This is a read-only property
     *
     * @param id the pipeline ID.
     * @return this
     */
    public PipelineDeployment setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * The pipeline node identifier.
     * <p>
     * This is the node that hosts the pipeline in cluster mode.
     *
     * @param node the pipeline node id
     * @return this
     */
    public PipelineDeployment setNode(String node) {
        this.node = node;
        return this;
    }

    /**
     * The pipeline name.
     * <p>
     * This is a normalized name (kebab-cased).
     *
     * @param name the pipeline name.
     * @return this
     */
    public PipelineDeployment setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * The pipeline control channel address.
     * <p>
     * This address allows to communicate signals to this particular pipeline and its components.
     * <p>
     * See vertx-pipeline-core about supported <code>Signal</code>s.
     *
     * @param controlChannel the control channel
     * @return this
     */
    public PipelineDeployment setControlChannel(String controlChannel) {
        this.controlChannel = controlChannel;
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        PipelineDeploymentConverter.toJson(this, json);
        return json;
    }

    @Override
    public int readFromBuffer(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        int start = pos + 4;
        String encoded = buffer.getString(start, start + length);
        PipelineDeploymentConverter.fromJson(new JsonObject(encoded), this);
        return pos + length + 4;
    }

    @Override
    public void writeToBuffer(Buffer buffer) {
        String encoded = toJson().toString();
        byte[] bytes = encoded.getBytes(StandardCharsets.UTF_8);
        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);
    }
}
