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

package fr.myprysm.pipeline.elasticsearch.sink;

import com.github.tomakehurst.wiremock.WireMockServer;
import fr.myprysm.pipeline.VertxTest;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;

class ElasticsearchSinkTest implements VertxTest {

    private static final String VERTICLE = "fr.myprysm.pipeline.elasticsearch.sink.ElasticsearchSink";
    public static final JsonObject CONFIG = obj()
            .put("name", "elasticsearch-sink")
            .put("type", VERTICLE)
            .put("from", "from")
            .put("to", arr().add("to"))
            .put("controlChannel", "channel")
            .put("indexName", "simple")
            .put("indexType", "test");
    private static WireMockServer WIREMOCK;

    @BeforeAll
    static void startWiremock() {
        // workaround for problem between ES netty and vertx (both wanting to set the same value)
        System.setProperty("es.set.netty.runtime.available.processors", "false");

        WIREMOCK = new WireMockServer(options().dynamicPort().usingFilesUnderClasspath("wiremock"));
        WIREMOCK.start();
        CONFIG.put("hosts", arr().add(obj().put("hostname", "localhost").put("port", WIREMOCK.port())));

        System.setProperty("es-sink-test-host", "127.0.0.1");
        System.setProperty("es-sink-test-port", String.valueOf(WIREMOCK.port()));
    }

    @Test
    @DisplayName("Elasticsearch sink should extract id from field")
    void itShouldExtractIdFromField(Vertx vertx, VertxTestContext ctx) {
        JsonObject config = CONFIG.copy().put("generateId", "field").put("field", "some.nested.field");
        vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(config), ctx.succeeding(id -> {
            vertx.eventBus().send("from", objectFromFile("wiremock/__files/extract-id.json"));
            vertx.setTimer(500, timer2 -> {
                ctx.verify(() -> WIREMOCK.verify(1, putRequestedFor(urlEqualTo("/simple/test/extracted-id?timeout=1m"))));
                ctx.completeNow();
            });
        }));
    }

    @Test
    @DisplayName("Elasticsearch sink should index documents")
    void itShouldIndexDocuments(Vertx vertx, VertxTestContext ctx) {
        JsonObject config = CONFIG.copy();
        vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(config), ctx.succeeding(id -> {
            vertx.eventBus().send("from", objectFromFile("wiremock/__files/single.json"));
            vertx.setTimer(500, timer2 -> {
                ctx.verify(() -> WIREMOCK.verify(1, postRequestedFor(urlEqualTo("/simple/test?timeout=1m"))));
                ctx.completeNow();
            });
        }));
    }

    @Test
    @DisplayName("Elasticsearch sink should bulk index documents")
    void itShouldBulkIndexDocuments(Vertx vertx, VertxTestContext ctx) {
        long bulk = 5L;
        JsonObject config = CONFIG.copy()
                .put("indexName", "bulk")
                .put("indexType", "test")
                .put("bulk", true)
                .put("bulkSize", bulk);
        vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(config), ctx.succeeding(id -> {
            arrayFromFile("wiremock/__files/bulk.json").stream()
                    .map(JsonObject.class::cast)
                    .forEach(event -> vertx.eventBus().send("from", event));

            vertx.setTimer(500, timer2 -> {
                ctx.verify(() -> WIREMOCK.verify(1, postRequestedFor(urlEqualTo("/_bulk?timeout=1m"))));
                ctx.completeNow();
            });
        }));
    }

    @Test
    @DisplayName("Elasticsearch sink should start with multiple hosts")
    void itShouldStartWithMultipleHosts(Vertx vertx, VertxTestContext ctx) {
        JsonObject config = CONFIG.copy()
                .put("hosts", arr()
                        .add(obj().put("hostname", "localhost").put("port", WIREMOCK.port()))
                        .add(obj().put("hostname", "http://127.0.0.1:9200"))
                        .add(obj().put("hostname", "127.0.0.1").put("port", WIREMOCK.port()).put("protocol", "http"))
                );

        vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(config), ctx.succeeding(id -> ctx.completeNow()));
    }

    @Test
    @DisplayName("Elasticsearch sink should start with host from environmment")
    void itShouldStartWithHostFromEnv(Vertx vertx, VertxTestContext ctx) {
        JsonObject config = CONFIG.copy()
                .put("hosts", arr().add(obj().put("hostname", "ENV:es-sink-test-host").put("port", "ENV:es-sink-test-port")));

        vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(config), ctx.succeeding(id -> ctx.completeNow()));
    }

    @Test
    @DisplayName("Elasticsearch sink should not start with invalid host")
    void itShouldNotStartWithInvalidHost(Vertx vertx, VertxTestContext ctx) {
        JsonObject config = CONFIG.copy()
                .put("hosts", arr().add(obj().put("hostname", "localhost").put("port", 65535)));

        vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(config), ctx.failing(id -> ctx.completeNow()));
    }

    @AfterAll
    static void stopWiremock() {
        WIREMOCK.stop();
    }
}