package fr.myprysm.pipeline.kotlin.processor

import fr.myprysm.pipeline.processor.ObjectToArrayProcessorOptions

fun ObjectToArrayProcessorOptions(
        fields: io.vertx.core.json.JsonArray? = null,
        instances: Int? = null,
        name: String? = null,
        type: String? = null): ObjectToArrayProcessorOptions = fr.myprysm.pipeline.processor.ObjectToArrayProcessorOptions().apply {

    if (fields != null) {
        this.setFields(fields)
    }
    if (instances != null) {
        this.setInstances(instances)
    }
    if (name != null) {
        this.setName(name)
    }
    if (type != null) {
        this.setType(type)
    }
}

