package fr.myprysm.pipeline.kotlin.processor

import fr.myprysm.pipeline.processor.ForkProcessorOptions

fun ForkProcessorOptions(
        instances: Int? = null,
        name: String? = null,
        publish: Iterable<String>? = null,
        send: Iterable<String>? = null,
        type: String? = null): ForkProcessorOptions = fr.myprysm.pipeline.processor.ForkProcessorOptions().apply {

    if (instances != null) {
        this.setInstances(instances)
    }
    if (name != null) {
        this.setName(name)
    }
    if (publish != null) {
        this.setPublish(publish.toList())
    }
    if (send != null) {
        this.setSend(send.toList())
    }
    if (type != null) {
        this.setType(type)
    }
}

