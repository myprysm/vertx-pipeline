package fr.myprysm.pipeline.kotlin.processor

import fr.myprysm.pipeline.processor.TimerEmitterProcessorOptions
import fr.myprysm.pipeline.util.Signal
import java.util.concurrent.TimeUnit

fun TimerEmitterProcessorOptions(
        delayTerminate: Long? = null,
        instances: Int? = null,
        interval: Long? = null,
        name: String? = null,
        signal: Signal? = null,
        type: String? = null,
        unit: TimeUnit? = null): TimerEmitterProcessorOptions = fr.myprysm.pipeline.processor.TimerEmitterProcessorOptions().apply {

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
    if (unit != null) {
        this.setUnit(unit)
    }
}

