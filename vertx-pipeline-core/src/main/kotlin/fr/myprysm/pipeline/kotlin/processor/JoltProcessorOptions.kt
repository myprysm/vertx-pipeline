package fr.myprysm.pipeline.kotlin.processor

import fr.myprysm.pipeline.processor.JoltProcessorOptions
import fr.myprysm.pipeline.processor.JoltProcessorOptions.Format

fun JoltProcessorOptions(
        format: Format? = null,
        instances: Int? = null,
        name: String? = null,
        path: String? = null,
        specs: io.vertx.core.json.JsonArray? = null,
        type: String? = null): JoltProcessorOptions = fr.myprysm.pipeline.processor.JoltProcessorOptions().apply {

    if (format != null) {
        this.setFormat(format)
    }
    if (instances != null) {
        this.setInstances(instances)
    }
    if (name != null) {
        this.setName(name)
    }
    if (path != null) {
        this.setPath(path)
    }
    if (specs != null) {
        this.setSpecs(specs)
    }
    if (type != null) {
        this.setType(type)
    }
}

