/*
 * Copyright (c) 2014 Red Hat, Inc. and others
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package fr.myprysm.pipeline.elasticsearch.sink;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link fr.myprysm.pipeline.elasticsearch.sink.ElasticsearchSinkOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.elasticsearch.sink.ElasticsearchSinkOptions} original class using Vert.x codegen.
 */
public class ElasticsearchSinkOptionsConverter {

  public static void fromJson(JsonObject json, ElasticsearchSinkOptions obj) {
    if (json.getValue("bulk") instanceof Boolean) {
      obj.setBulk((Boolean)json.getValue("bulk"));
    }
    if (json.getValue("bulkSize") instanceof Number) {
      obj.setBulkSize(((Number)json.getValue("bulkSize")).intValue());
    }
    if (json.getValue("field") instanceof String) {
      obj.setField((String)json.getValue("field"));
    }
    if (json.getValue("generateId") instanceof String) {
      obj.setGenerateId(fr.myprysm.pipeline.elasticsearch.sink.ElasticsearchSinkOptions.Strategy.valueOf((String)json.getValue("generateId")));
    }
    if (json.getValue("hosts") instanceof JsonArray) {
      obj.setHosts(((JsonArray)json.getValue("hosts")).copy());
    }
    if (json.getValue("indexName") instanceof String) {
      obj.setIndexName((String)json.getValue("indexName"));
    }
    if (json.getValue("indexType") instanceof String) {
      obj.setIndexType((String)json.getValue("indexType"));
    }
  }

  public static void toJson(ElasticsearchSinkOptions obj, JsonObject json) {
    if (obj.getBulk() != null) {
      json.put("bulk", obj.getBulk());
    }
    if (obj.getBulkSize() != null) {
      json.put("bulkSize", obj.getBulkSize());
    }
    if (obj.getField() != null) {
      json.put("field", obj.getField());
    }
    if (obj.getGenerateId() != null) {
      json.put("generateId", obj.getGenerateId().name());
    }
    if (obj.getHosts() != null) {
      json.put("hosts", obj.getHosts());
    }
    if (obj.getIndexName() != null) {
      json.put("indexName", obj.getIndexName());
    }
    if (obj.getIndexType() != null) {
      json.put("indexType", obj.getIndexType());
    }
  }
}