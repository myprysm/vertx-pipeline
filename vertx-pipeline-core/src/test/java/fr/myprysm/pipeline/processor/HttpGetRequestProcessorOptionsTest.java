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

package fr.myprysm.pipeline.processor;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.OnError.CONTINUE;
import static fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.Protocol.HTTP;
import static fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.Protocol.HTTPS;
import static fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.ResponseType.LIST;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class HttpGetRequestProcessorOptionsTest {

    @Test
    @DisplayName("Validate HTTP Get Request options")
    void testHttpGetRequestProcessorOptions() {
        new HttpGetRequestProcessorOptionsConverter();

        String badStr = "{\"name\": 10, \"type\": 20, \"extract\": false}";
        String optStr = "{\"instances\":1,\"name\":\"name\",\"type\":\"type\",\"headers\":{\"header1\":\"header-value1\"},\"host\":\"localhost\",\"injection\":\"inject\",\"onError\":\"CONTINUE\",\"pathParams\":{\":param1\":\"$event.value1\"},\"port\":80,\"protocol\":\"HTTP\",\"queryParams\":{\"param1\":\"param-value1\"},\"responseType\":\"LIST\",\"url\":\"/\",\"userAgent\":\"user-agent\"}";

        ProcessorOptions optPump = new ProcessorOptions(new JsonObject(optStr));
        HttpGetRequestProcessorConfigurer configurer = new HttpGetRequestProcessorConfigurer(new JsonObject(optStr));

        HttpGetRequestProcessorOptions optNull = new HttpGetRequestProcessorOptions()
                .setName(null)
                .setType(null)
                .setHeaders(null)
                .setHost(null)
                .setInjection(null)
                .setOnError(null)
                .setPathParams(null)
                .setPort(null)
                .setProtocol(null)
                .setQueryParams(null)
                .setUrl(null)
                .setUserAgent(null)
                .setResponseType(null);

        HttpGetRequestProcessorOptions optObj = new HttpGetRequestProcessorOptions()
                .setName("name")
                .setType("type")
                .setHeaders(obj().put("header1", "header-value1"))
                .setHost("localhost")
                .setInjection("inject")
                .setOnError(CONTINUE)
                .setPathParams(obj().put(":param1", "$event.value1"))
                .setPort(80)
                .setProtocol(HTTP)
                .setQueryParams(obj().put("param1", "param-value1"))
                .setUrl("/")
                .setUserAgent("user-agent")
                .setResponseType(LIST);

        HttpGetRequestProcessorOptions optObjHttps = new HttpGetRequestProcessorOptions()
                .setName("name")
                .setType("type")
                .setHeaders(obj().put("header1", "header-value1"))
                .setHost("localhost")
                .setInjection("inject")
                .setOnError(CONTINUE)
                .setPathParams(obj().put(":param1", "$event.value1"))
                .setPort(443)
                .setProtocol(HTTPS)
                .setQueryParams(obj().put("param1", "param-value1"))
                .setUrl("/")
                .setUserAgent("user-agent")
                .setResponseType(LIST);

        HttpGetRequestProcessorConfigurer optObjHttpsOtherPort = new HttpGetRequestProcessorConfigurer(new HttpGetRequestProcessorOptions()
                .setName("name")
                .setType("type")
                .setHeaders(obj().put("header1", "header-value1"))
                .setHost("localhost")
                .setInjection("inject")
                .setOnError(CONTINUE)
                .setPathParams(obj().put(":param1", "$event.value1"))
                .setPort(8443)
                .setProtocol(HTTPS)
                .setQueryParams(obj().put("param1", "param-value1"))
                .setUrl("/")
                .setUserAgent("user-agent")
                .setResponseType(LIST));

        assertThat(optObjHttpsOtherPort.getClientOptions().getDefaultPort()).isEqualTo(8443);

        JsonObject optJson = new JsonObject(optStr);

        assertThat(optObjHttpsOtherPort.getProtocol().protocol()).isEqualTo("https");

        assertThat(configurer).isEqualTo(new HttpGetRequestProcessorConfigurer(configurer));
        assertThat(configurer).isNotEqualTo(new HttpGetRequestProcessorConfigurer(optObjHttps));
        assertThat(new HttpGetRequestProcessorOptions(new JsonObject(badStr))).isEqualTo(new HttpGetRequestProcessorOptions());
        assertThat(optNull.toJson()).isEqualTo(obj().put("instances", 1));
        assertThat(optObj).isEqualTo(optObj);
        assertThat(optObj).isNotEqualTo(optNull);
        System.out.println(optObj.toJson().toString());
        assertThat(optObj).isEqualTo(new HttpGetRequestProcessorOptions(optJson));
        assertThat(optObj).isEqualTo(new HttpGetRequestProcessorOptions(optObj));
        assertThat(optObj.toString()).isEqualTo(new HttpGetRequestProcessorOptions(optJson).toString());
        assertThat(optObj.hashCode()).isEqualTo(new HttpGetRequestProcessorOptions(optJson).hashCode());
        assertThat(optObj).isNotEqualTo(null);
        assertThat(optObj).isNotEqualTo(new Object());
        assertThat(optObj.toJson()).isEqualTo(optJson);
        assertThat(optObj).isEqualTo((HttpGetRequestProcessorOptions) configurer);
        assertThat(optPump)
                .isEqualToComparingOnlyGivenFields(new HttpGetRequestProcessorOptions(optPump), "name", "type");
    }

}