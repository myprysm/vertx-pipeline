package fr.myprysm.pipeline.kotlin.pump

import fr.myprysm.pipeline.pump.EventBusPumpOptions

fun EventBusPumpOptions(
        address: String? = null,
        name: String? = null,
        type: String? = null): EventBusPumpOptions = fr.myprysm.pipeline.pump.EventBusPumpOptions().apply {

    if (address != null) {
        this.setAddress(address)
    }
    if (name != null) {
        this.setName(name)
    }
    if (type != null) {
        this.setType(type)
    }
}

