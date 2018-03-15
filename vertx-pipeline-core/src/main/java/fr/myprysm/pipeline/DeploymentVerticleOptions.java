/*
 * Copyright 2018 the original author or the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.myprysm.pipeline;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject(generateConverter = true)
public class DeploymentVerticleOptions {

    public static final Boolean DEFAULT_METRICS = false;
    public static final Boolean DEFAULT_JMX_ENABLED = false;
//    public static final Boolean DEFAULT_GLOBAL_METRICS = true;

    private Boolean metrics = DEFAULT_METRICS;
    private Boolean jmxEnabled = DEFAULT_JMX_ENABLED;
//    private Boolean global;

    public DeploymentVerticleOptions() {

    }

    public DeploymentVerticleOptions(DeploymentVerticleOptions other) {
        metrics = other.metrics;
        jmxEnabled = other.jmxEnabled;
    }

    public DeploymentVerticleOptions(JsonObject json) {
        DeploymentVerticleOptionsConverter.fromJson(json, this);
    }

    public Boolean getMetrics() {
        return metrics;
    }

    public DeploymentVerticleOptions setMetrics(Boolean metrics) {
        this.metrics = metrics;
        return this;
    }

    public Boolean getJmxEnabled() {
        return jmxEnabled;
    }

    public DeploymentVerticleOptions setJmxEnabled(Boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        DeploymentVerticleOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeploymentVerticleOptions)) return false;
        DeploymentVerticleOptions that = (DeploymentVerticleOptions) o;
        return Objects.equals(metrics, that.metrics) &&
                Objects.equals(jmxEnabled, that.jmxEnabled);
    }

    @Override
    public int hashCode() {

        return Objects.hash(metrics, jmxEnabled);
    }

    @Override
    public String toString() {
        return "DeploymentVerticleOptions{" +
                "metrics=" + metrics +
                ", jmxEnabled=" + jmxEnabled +
                '}';
    }
}
