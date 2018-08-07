package fr.myprysm.pipeline.kotlin.processor

import fr.myprysm.pipeline.processor.MergeBasicProcessorOptions

fun MergeBasicProcessorOptions(
        defaultCapacity: Long? = null,
        instances: Int? = null,
        name: String? = null,
        onFlush: io.vertx.core.json.JsonObject? = null,
        operations: io.vertx.core.json.JsonObject? = null,
        type: String? = null): MergeBasicProcessorOptions = fr.myprysm.pipeline.processor.MergeBasicProcessorOptions().apply {

    if (defaultCapacity != null) {
        this.setDefaultCapacity(defaultCapacity)
    }
    if (instances != null) {
        this.setInstances(instances)
    }
    if (name != null) {
        this.setName(name)
    }
    if (onFlush != null) {
        this.setOnFlush(onFlush)
    }
    if (operations != null) {
        this.setOperations(operations)
    }
    if (type != null) {
        this.setType(type)
    }
}

