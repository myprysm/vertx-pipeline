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

package fr.myprysm.pipeline.pump;

import fr.myprysm.pipeline.datasource.DatasourceConfiguration;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject(generateConverter = true)
public class DatasourcePumpOptions extends PumpOptions {

    private DatasourceConfiguration datasource;

    public DatasourcePumpOptions() {
        super();
    }

    public DatasourcePumpOptions(DatasourcePumpOptions other) {
        super(other);
        datasource = other.datasource;
    }

    public DatasourcePumpOptions(PumpOptions other) {
        super(other);
    }

    public DatasourcePumpOptions(JsonObject json) {
        super(json);
        DatasourcePumpOptionsConverter.fromJson(json, this);
    }

    public DatasourceConfiguration getDatasource() {
        return datasource;
    }

    public DatasourcePumpOptions setDatasource(DatasourceConfiguration datasource) {
        this.datasource = datasource;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public DatasourcePumpOptions setName(String name) {
        return (DatasourcePumpOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public DatasourcePumpOptions setType(String type) {
        return (DatasourcePumpOptions) super.setType(type);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        DatasourcePumpOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatasourcePumpOptions)) return false;
        if (!super.equals(o)) return false;
        DatasourcePumpOptions that = (DatasourcePumpOptions) o;
        return Objects.equals(datasource, that.datasource);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), datasource);
    }

    @Override
    public String toString() {
        return "DatasourcePumpOptions{" +
                "datasource=" + datasource +
                "} " + super.toString();
    }
}
