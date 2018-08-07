package fr.myprysm.pipeline.kotlin

import fr.myprysm.pipeline.DeploymentVerticleOptions

fun DeploymentVerticleOptions(
        jmxEnabled: Boolean? = null,
        metrics: Boolean? = null): DeploymentVerticleOptions = fr.myprysm.pipeline.DeploymentVerticleOptions().apply {

    if (jmxEnabled != null) {
        this.setJmxEnabled(jmxEnabled)
    }
    if (metrics != null) {
        this.setMetrics(metrics)
    }
}

