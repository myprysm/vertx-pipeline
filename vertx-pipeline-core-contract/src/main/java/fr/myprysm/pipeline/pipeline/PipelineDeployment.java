package fr.myprysm.pipeline.pipeline;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Response provided when a pipeline is successfully deployed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@DataObject(generateConverter = true)
public class PipelineDeployment {

    private String name;
    private String controlChannel;

    public PipelineDeployment(PipelineDeployment other) {
        name = other.name;
        controlChannel = other.controlChannel;
    }

    public PipelineDeployment(JsonObject json) {
        PipelineDeploymentConverter.fromJson(json, this);
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
     * @param controlChannel the pipeline name.
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
}
