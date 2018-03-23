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

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link fr.myprysm.pipeline.pump.DatasourcePumpOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.pump.DatasourcePumpOptions} original class using Vert.x codegen.
 */
public class DatasourcePumpOptionsConverter {

  public static void fromJson(JsonObject json, DatasourcePumpOptions obj) {
    if (json.getValue("datasource") instanceof JsonObject) {
      obj.setDatasource(new fr.myprysm.pipeline.datasource.DatasourceConfiguration((JsonObject)json.getValue("datasource")));
    }
  }

  public static void toJson(DatasourcePumpOptions obj, JsonObject json) {
    if (obj.getDatasource() != null) {
      json.put("datasource", obj.getDatasource().toJson());
    }
  }
}