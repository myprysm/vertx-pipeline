package fr.myprysm.pipeline.kotlin.pipeline

import fr.myprysm.pipeline.pipeline.ExchangeOptions

fun ExchangeOptions(
        controlChannel: String? = null,
        from: String? = null,
        to: Iterable<String>? = null): ExchangeOptions = fr.myprysm.pipeline.pipeline.ExchangeOptions().apply {

    if (controlChannel != null) {
        this.setControlChannel(controlChannel)
    }
    if (from != null) {
        this.setFrom(from)
    }
    if (to != null) {
        this.setTo(to.toList())
    }
}

