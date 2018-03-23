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
public class DatasourceRegistration {

    private String deployment;
    private String component;
    private String alias;
    private String configuration;

    public DatasourceRegistration() {
    }

    public DatasourceRegistration(DatasourceRegistration other) {
        deployment = other.deployment;
        component = other.component;
        alias = other.alias;
        configuration = other.configuration;
    }

    public DatasourceRegistration(JsonObject json) {
        DatasourceRegistrationConverter.fromJson(json, this);
    }

    public String getDeployment() {
        return deployment;
    }

    public DatasourceRegistration setDeployment(String deployment) {
        this.deployment = deployment;
        return this;
    }

    public String getComponent() {
        return component;
    }

    public DatasourceRegistration setComponent(String component) {
        this.component = component;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public DatasourceRegistration setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public String getConfiguration() {
        return configuration;
    }

    public DatasourceRegistration setConfiguration(String configuration) {
        this.configuration = configuration;
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = obj();
        DatasourceRegistrationConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatasourceRegistration)) return false;
        DatasourceRegistration that = (DatasourceRegistration) o;
        return Objects.equals(deployment, that.deployment) &&
                Objects.equals(component, that.component) &&
                Objects.equals(alias, that.alias) &&
                Objects.equals(configuration, that.configuration);
    }

    @Override
    public int hashCode() {

        return Objects.hash(deployment, component, alias, configuration);
    }

    @Override
    public String toString() {
        return "DatasourceRegistration{" +
                "deployment='" + deployment + '\'' +
                ", component='" + component + '\'' +
                ", alias='" + alias + '\'' +
                ", configuration='" + configuration + '\'' +
                '}';
    }
}
