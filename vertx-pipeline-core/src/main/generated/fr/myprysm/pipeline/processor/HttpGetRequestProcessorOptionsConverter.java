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

package fr.myprysm.pipeline.processor;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions} original class using Vert.x codegen.
 */
public class HttpGetRequestProcessorOptionsConverter {

  public static void fromJson(JsonObject json, HttpGetRequestProcessorOptions obj) {
    if (json.getValue("headers") instanceof JsonObject) {
      obj.setHeaders(((JsonObject)json.getValue("headers")).copy());
    }
    if (json.getValue("host") instanceof String) {
      obj.setHost((String)json.getValue("host"));
    }
    if (json.getValue("injection") instanceof String) {
      obj.setInjection((String)json.getValue("injection"));
    }
    if (json.getValue("onError") instanceof String) {
      obj.setOnError(fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.OnError.valueOf((String)json.getValue("onError")));
    }
    if (json.getValue("pathParams") instanceof JsonObject) {
      obj.setPathParams(((JsonObject)json.getValue("pathParams")).copy());
    }
    if (json.getValue("port") instanceof Number) {
      obj.setPort(((Number)json.getValue("port")).intValue());
    }
    if (json.getValue("protocol") instanceof String) {
      obj.setProtocol(fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.Protocol.valueOf((String)json.getValue("protocol")));
    }
    if (json.getValue("queryParams") instanceof JsonObject) {
      obj.setQueryParams(((JsonObject)json.getValue("queryParams")).copy());
    }
    if (json.getValue("responseType") instanceof String) {
      obj.setResponseType(fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.ResponseType.valueOf((String)json.getValue("responseType")));
    }
    if (json.getValue("url") instanceof String) {
      obj.setUrl((String)json.getValue("url"));
    }
    if (json.getValue("userAgent") instanceof String) {
      obj.setUserAgent((String)json.getValue("userAgent"));
    }
  }

  public static void toJson(HttpGetRequestProcessorOptions obj, JsonObject json) {
    if (obj.getHeaders() != null) {
      json.put("headers", obj.getHeaders());
    }
    if (obj.getHost() != null) {
      json.put("host", obj.getHost());
    }
    if (obj.getInjection() != null) {
      json.put("injection", obj.getInjection());
    }
    if (obj.getOnError() != null) {
      json.put("onError", obj.getOnError().name());
    }
    if (obj.getPathParams() != null) {
      json.put("pathParams", obj.getPathParams());
    }
    if (obj.getPort() != null) {
      json.put("port", obj.getPort());
    }
    if (obj.getProtocol() != null) {
      json.put("protocol", obj.getProtocol().name());
    }
    if (obj.getQueryParams() != null) {
      json.put("queryParams", obj.getQueryParams());
    }
    if (obj.getResponseType() != null) {
      json.put("responseType", obj.getResponseType().name());
    }
    if (obj.getUrl() != null) {
      json.put("url", obj.getUrl());
    }
    if (obj.getUserAgent() != null) {
      json.put("userAgent", obj.getUserAgent());
    }
  }
}