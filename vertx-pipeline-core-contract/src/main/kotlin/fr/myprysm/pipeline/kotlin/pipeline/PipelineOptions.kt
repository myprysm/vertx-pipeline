package fr.myprysm.pipeline.kotlin.pipeline

import fr.myprysm.pipeline.pipeline.PipelineOptions

fun PipelineOptions(
        deployChannel: String? = null,
        name: String? = null,
        processors: io.vertx.core.json.JsonArray? = null,
        pump: io.vertx.core.json.JsonObject? = null,
        sink: io.vertx.core.json.JsonObject? = null): PipelineOptions = fr.myprysm.pipeline.pipeline.PipelineOptions().apply {

    if (deployChannel != null) {
        this.setDeployChannel(deployChannel)
    }
    if (name != null) {
        this.setName(name)
    }
    if (processors != null) {
        this.setProcessors(processors)
    }
    if (pump != null) {
        this.setPump(pump)
    }
    if (sink != null) {
        this.setSink(sink)
    }
}

