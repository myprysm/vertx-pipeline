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
 * Converter for {@link fr.myprysm.pipeline.datasource.DatasourceRegistration}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.datasource.DatasourceRegistration} original class using Vert.x codegen.
 */
public class DatasourceRegistrationConverter {

  public static void fromJson(JsonObject json, DatasourceRegistration obj) {
    if (json.getValue("alias") instanceof String) {
      obj.setAlias((String)json.getValue("alias"));
    }
    if (json.getValue("component") instanceof String) {
      obj.setComponent((String)json.getValue("component"));
    }
    if (json.getValue("configuration") instanceof String) {
      obj.setConfiguration((String)json.getValue("configuration"));
    }
    if (json.getValue("deployment") instanceof String) {
      obj.setDeployment((String)json.getValue("deployment"));
    }
  }

  public static void toJson(DatasourceRegistration obj, JsonObject json) {
    if (obj.getAlias() != null) {
      json.put("alias", obj.getAlias());
    }
    if (obj.getComponent() != null) {
      json.put("component", obj.getComponent());
    }
    if (obj.getConfiguration() != null) {
      json.put("configuration", obj.getConfiguration());
    }
    if (obj.getDeployment() != null) {
      json.put("deployment", obj.getDeployment());
    }
  }
}