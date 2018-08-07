package fr.myprysm.pipeline.kotlin.processor

import fr.myprysm.pipeline.processor.ProcessorOptions

fun ProcessorOptions(
        instances: Int? = null,
        name: String? = null,
        type: String? = null): ProcessorOptions = fr.myprysm.pipeline.processor.ProcessorOptions().apply {

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

