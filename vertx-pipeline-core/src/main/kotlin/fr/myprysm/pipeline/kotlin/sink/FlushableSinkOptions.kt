package fr.myprysm.pipeline.kotlin.sink

import fr.myprysm.pipeline.sink.FlushableSinkOptions

fun FlushableSinkOptions(
        batchSize: Int? = null,
        name: String? = null,
        type: String? = null): FlushableSinkOptions = fr.myprysm.pipeline.sink.FlushableSinkOptions().apply {

    if (batchSize != null) {
        this.setBatchSize(batchSize)
    }
    if (name != null) {
        this.setName(name)
    }
    if (type != null) {
        this.setType(type)
    }
}

