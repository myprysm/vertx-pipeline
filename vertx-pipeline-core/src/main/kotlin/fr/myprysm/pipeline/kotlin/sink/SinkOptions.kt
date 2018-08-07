package fr.myprysm.pipeline.kotlin.sink

import fr.myprysm.pipeline.sink.SinkOptions

fun SinkOptions(
        name: String? = null,
        type: String? = null): SinkOptions = fr.myprysm.pipeline.sink.SinkOptions().apply {

    if (name != null) {
        this.setName(name)
    }
    if (type != null) {
        this.setType(type)
    }
}

