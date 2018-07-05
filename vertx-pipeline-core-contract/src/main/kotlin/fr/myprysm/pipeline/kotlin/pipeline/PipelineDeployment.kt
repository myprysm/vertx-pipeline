package fr.myprysm.pipeline.kotlin.pipeline

import fr.myprysm.pipeline.pipeline.PipelineDeployment

/**
 * A function providing a DSL for building [fr.myprysm.pipeline.pipeline.PipelineDeployment] objects.
 *
 * Response provided when a pipeline is successfully deployed.
 *
 * @param controlChannel  The pipeline control channel address. <p> This address allows to communicate signals to this particular pipeline and its components. <p> See vertx-pipeline-core about supported <code>Signal</code>s.
 * @param id  The pipeline identifier. <p> This is basically the deployment ID for the <code>PipelineVerticle</code>. <p> This is a read-only property
 * @param name  The pipeline name. <p> This is a normalized name (kebab-cased).
 * @param node  The pipeline node identifier. <p> This is the node that hosts the pipeline in cluster mode.
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [fr.myprysm.pipeline.pipeline.PipelineDeployment original] using Vert.x codegen.
 */
fun PipelineDeployment(
        controlChannel: String? = null,
        id: String? = null,
        name: String? = null,
        node: String? = null): PipelineDeployment = fr.myprysm.pipeline.pipeline.PipelineDeployment().apply {

    if (controlChannel != null) {
        this.setControlChannel(controlChannel)
    }
    if (id != null) {
        this.setId(id)
    }
    if (name != null) {
        this.setName(name)
    }
    if (node != null) {
        this.setNode(node)
    }
}

