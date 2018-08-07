package fr.myprysm.pipeline.kotlin.sink

import fr.myprysm.pipeline.sink.EventBusSinkOptions

fun EventBusSinkOptions(
        name: String? = null,
        publish: Iterable<String>? = null,
        send: Iterable<String>? = null,
        type: String? = null): EventBusSinkOptions = fr.myprysm.pipeline.sink.EventBusSinkOptions().apply {

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

