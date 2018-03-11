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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class HttpGetRequestProcessorConfigurerTest {

    static HttpGetRequestProcessorConfigurer CONFIGURER = new HttpGetRequestProcessorConfigurer(obj()
            .put("port", 8271)
            .put("injection", "another.place")
            .put("responseType", "LIST")
            .put("userAgent", "Vert.X Pipeline Test agent")
            .put("url", "/some/long/url/with/tick/:tick/counter/:counter/type/:type")
            .put("headers", obj()
                    .put("X-Test-Tick", "$event.type")
                    .put("X-Test-Counter", "$event.counter")
                    .put("X-Test-Type", "$event.type")
                    .put("X-Test-Value", "a value")
            )
            .put("queryParams", obj()
                    .put("foo", "bar")
                    .put("tick", "$event.tick")
                    .put("counter", "$event.counter")
                    .put("type", "$event.type")
            )
            .put("pathParams", obj()
                    .put("tick", "$event.tick")
                    .put("counter", "$event.counter")
                    .put("type", "$event.type")
                    .put("unmapped", "some unmapped path param")
            )
    );

    @Test
    @DisplayName("The configurer has one default header and three headers to extract")
    void configurerHasOneDefaultHeaderAndThreeHeadersToExtract() {
        assertThat(CONFIGURER.getDefaultHeaders()).hasSize(1);
        assertThat(CONFIGURER.getDefaultHeaders().get("X-Test-Value")).isEqualTo("a value");

        assertThat(CONFIGURER.getExtractHeaders()).hasSize(3);
        assertThat(CONFIGURER.getExtractHeaders().get("X-Test-Type")).isEqualTo("type");
    }


    @Test
    @DisplayName("The configurer has one default query parameter and three parameters to extract")
    void configurerHasThreeQueryParametersAndOneDefaultParameter() {
        assertThat(CONFIGURER.getDefaultQueryParams()).hasSize(1);
        assertThat(CONFIGURER.getDefaultQueryParams().get("foo")).isEqualTo("bar");

        assertThat(CONFIGURER.getExtractQueryParams()).hasSize(3);
        assertThat(CONFIGURER.getExtractQueryParams().get("counter")).isEqualTo("counter");
    }

    @Test
    @DisplayName("The configurer has one default query parameter and three parameters to extract")
    void configurerHasThreePathParameters() {
        assertThat(CONFIGURER.getExtractPathParams()).hasSize(3);
        assertThat(CONFIGURER.getExtractPathParams().get("type")).isEqualTo("type");
    }

    @Test
    @DisplayName("The configurer has six url fragments")
    void configurerHasSixUrlFragments() {
        List<String> fragments = CONFIGURER.getUrlFragments();

        assertThat(fragments).hasSize(6);
        assertThat(fragments.get(0)).isEqualTo("/some/long/url/with/tick/");
        assertThat(fragments.get(1)).isEqualTo("tick");
        assertThat(fragments.get(2)).isEqualTo("/counter/");
        assertThat(fragments.get(3)).isEqualTo("counter");
        assertThat(fragments.get(4)).isEqualTo("/type/");
        assertThat(fragments.get(5)).isEqualTo("type");
    }
}