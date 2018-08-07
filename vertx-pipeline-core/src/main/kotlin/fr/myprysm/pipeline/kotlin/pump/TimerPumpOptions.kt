package fr.myprysm.pipeline.kotlin.pump

import fr.myprysm.pipeline.pump.TimerPumpOptions
import java.util.concurrent.TimeUnit

fun TimerPumpOptions(
        data: io.vertx.core.json.JsonObject? = null,
        interval: Long? = null,
        name: String? = null,
        type: String? = null,
        unit: TimeUnit? = null): TimerPumpOptions = fr.myprysm.pipeline.pump.TimerPumpOptions().apply {

    if (data != null) {
        this.setData(data)
    }
    if (interval != null) {
        this.setInterval(interval)
    }
    if (name != null) {
        this.setName(name)
    }
    if (type != null) {
        this.setType(type)
    }
    if (unit != null) {
        this.setUnit(unit)
    }
}

