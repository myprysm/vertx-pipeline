package fr.myprysm.pipeline.kotlin.pump

import fr.myprysm.pipeline.pump.CronPumpOptions

fun CronPumpOptions(
        cron: String? = null,
        data: io.vertx.core.json.JsonObject? = null,
        emitter: String? = null,
        name: String? = null,
        type: String? = null): CronPumpOptions = fr.myprysm.pipeline.pump.CronPumpOptions().apply {

    if (cron != null) {
        this.setCron(cron)
    }
    if (data != null) {
        this.setData(data)
    }
    if (emitter != null) {
        this.setEmitter(emitter)
    }
    if (name != null) {
        this.setName(name)
    }
    if (type != null) {
        this.setType(type)
    }
}

