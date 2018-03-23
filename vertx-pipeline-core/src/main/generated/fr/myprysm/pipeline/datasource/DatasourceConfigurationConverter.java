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

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link fr.myprysm.pipeline.datasource.DatasourceConfiguration}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.datasource.DatasourceConfiguration} original class using Vert.x codegen.
 */
public class DatasourceConfigurationConverter {

  public static void fromJson(JsonObject json, DatasourceConfiguration obj) {
    if (json.getValue("deployment") instanceof String) {
      obj.setDeployment((String)json.getValue("deployment"));
    }
    if (json.getValue("name") instanceof String) {
      obj.setName((String)json.getValue("name"));
    }
    if (json.getValue("properties") instanceof JsonObject) {
      obj.setProperties(((JsonObject)json.getValue("properties")).copy());
    }
  }

  public static void toJson(DatasourceConfiguration obj, JsonObject json) {
    if (obj.getDeployment() != null) {
      json.put("deployment", obj.getDeployment());
    }
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
    if (obj.getProperties() != null) {
      json.put("properties", obj.getProperties());
    }
  }
}