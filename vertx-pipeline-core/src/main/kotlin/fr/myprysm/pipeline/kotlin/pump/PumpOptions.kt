package fr.myprysm.pipeline.kotlin.pump

import fr.myprysm.pipeline.pump.PumpOptions

fun PumpOptions(
        name: String? = null,
        type: String? = null): PumpOptions = fr.myprysm.pipeline.pump.PumpOptions().apply {

    if (name != null) {
        this.setName(name)
    }
    if (type != null) {
        this.setType(type)
    }
}

