package fr.myprysm.pipeline.kotlin.pipeline

import fr.myprysm.pipeline.pipeline.PipelineDeployment

/**
 * A function providing a DSL for building [fr.myprysm.pipeline.pipeline.PipelineDeployment] objects.
 *
 * Response provided when a pipeline is successfully deployed.
 *
 * @param controlChannel  The pipeline control channel address. <p> This address allows to communicate signals to this particular pipeline. <p> See vertx-pipeline-core about supported <code>Signal</code>s.
 * @param name  The pipeline name. <p> This is a normalized name (kebab-cased).
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [fr.myprysm.pipeline.pipeline.PipelineDeployment original] using Vert.x codegen.
 */
fun PipelineDeployment(
        controlChannel: String? = null,
        name: String? = null): PipelineDeployment = fr.myprysm.pipeline.pipeline.PipelineDeployment().apply {

    if (controlChannel != null) {
        this.setControlChannel(controlChannel)
    }
    if (name != null) {
        this.setName(name)
    }
}

