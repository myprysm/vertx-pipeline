package fr.myprysm.pipeline.kotlin.processor

import fr.myprysm.pipeline.processor.DataExtractorProcessorOptions

fun DataExtractorProcessorOptions(
        extract: io.vertx.core.json.JsonObject? = null,
        instances: Int? = null,
        name: String? = null,
        type: String? = null): DataExtractorProcessorOptions = fr.myprysm.pipeline.processor.DataExtractorProcessorOptions().apply {

    if (extract != null) {
        this.setExtract(extract)
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

