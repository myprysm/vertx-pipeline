package fr.myprysm.pipeline.kotlin.processor

import fr.myprysm.pipeline.processor.CounterEmitterProcessorOptions
import fr.myprysm.pipeline.util.Signal

fun CounterEmitterProcessorOptions(
        delayTerminate: Long? = null,
        instances: Int? = null,
        interval: Long? = null,
        name: String? = null,
        signal: Signal? = null,
        type: String? = null): CounterEmitterProcessorOptions = fr.myprysm.pipeline.processor.CounterEmitterProcessorOptions().apply {

    if (delayTerminate != null) {
        this.setDelayTerminate(delayTerminate)
    }
    if (instances != null) {
        this.setInstances(instances)
    }
    if (interval != null) {
        this.setInterval(interval)
    }
    if (name != null) {
        this.setName(name)
    }
    if (signal != null) {
        this.setSignal(signal)
    }
    if (type != null) {
        this.setType(type)
    }
}

