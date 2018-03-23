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

package fr.myprysm.pipeline.datasource;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;

@DataObject(generateConverter = true)
public class DatasourceConfiguration {

    private String name;
    private String deployment;
    private JsonObject properties;

    public DatasourceConfiguration() {

    }

    public DatasourceConfiguration(DatasourceConfiguration other) {
        name = other.name;
        deployment = other.deployment;
        properties = other.properties;
    }

    public DatasourceConfiguration(JsonObject json) {
        DatasourceConfigurationConverter.fromJson(json, this);
    }

    /**
     * The name of the configuration
     *
     * @return the name of the configuration
     */
    public String getName() {
        return name;
    }

    /**
     * The name of the configuration
     * <p>
     * This must be unique across the instance(s)
     *
     * @param name the name of the configuration
     * @return this
     */
    public DatasourceConfiguration setName(String name) {
        this.name = name;
        return this;
    }

    public String getDeployment() {
        return deployment;
    }

    public DatasourceConfiguration setDeployment(String deployment) {
        this.deployment = deployment;
        return this;
    }

    /**
     * The properties of the datasource as a json object.
     *
     * @return the properties of the datasource
     */
    public JsonObject getProperties() {
        return properties;
    }

    /**
     * The properties of the datasource as a json object.
     * <p>
     * This allows datasource configuration to be serialized across the event bus
     *
     * @param properties the properties of the datasource
     * @return this
     */
    public DatasourceConfiguration setProperties(JsonObject properties) {
        this.properties = properties;
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = obj();
        DatasourceConfigurationConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatasourceConfiguration)) return false;
        DatasourceConfiguration that = (DatasourceConfiguration) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(deployment, that.deployment) &&
                Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, deployment, properties);
    }

    @Override
    public String toString() {
        return "DatasourceConfiguration{" +
                "name='" + name + '\'' +
                ", deployment='" + deployment + '\'' +
                ", properties=" + properties +
                '}';
    }

}
