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

import fr.myprysm.pipeline.VertxTest;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.Netty4Plugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class ElasticsearchSinkTest implements VertxTest {
    private static Node node;
    private static Client client;

    private static final String VERTICLE = "fr.myprysm.pipeline.elasticsearch.sink.ElasticsearchSink";
    public static final JsonObject CONFIG = obj()
            .put("name", "elasticsearch-sink")
            .put("type", VERTICLE)
            .put("from", "from")
            .put("to", arr().add("to"))
            .put("controlChannel", "channel")
            .put("indexName", "simple")
            .put("indexType", "test");

    @BeforeAll
    static void startElasticSearch() throws NodeValidationException {
        // workaround for problem between ES netty and vertx (both wanting to set the same value)
        System.setProperty("es.set.netty.runtime.available.processors", "false");

        node = new MyNode(
                Settings.builder()
                        .put("transport.type", "netty4")
                        .put("http.type", "netty4")
                        .put("http.enabled", "true")
                        .put("path.home", "elasticsearch-data")
                        .build(),
                asList(Netty4Plugin.class));
        client = node.start().client();
    }

    @Test
    @DisplayName("Elasticsearch sink should index documents")
    void elasticsearchSinkShouldIndexDocuments(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        JsonObject config = CONFIG.copy();
        vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(config), id -> {
            vertx.eventBus().send("from", obj().put("foo", "bar"));
            vertx.setTimer(3000, timer2 -> {
                client.search(new SearchRequest().indices("simple").types("test"), new ActionListener<SearchResponse>() {
                    @Override
                    public void onResponse(SearchResponse searchResponse) {
                        ctx.verify(() -> assertThat(searchResponse.getHits().totalHits).isEqualTo(1));
                        ctx.completeNow();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ctx.failNow(e);
                    }
                });
            });
        });
        ctx.awaitCompletion(1, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("Elasticsearch sink should bulk index documents")
    void elasticsearchSinkShouldBulkIndexDocuments(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        long bulk = 5L;
        JsonObject config = CONFIG.copy()
                .put("indexName", "bulk")
                .put("indexType", "test")
                .put("bulk", true)
                .put("bulkSize", bulk);
        vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(config), id -> {

            IntStream.range(0, (int) bulk).forEach(i -> vertx.eventBus().send("from", obj().put("foo", "bar")
                    .put("time", System.currentTimeMillis())));

            vertx.setTimer(3000, timer2 -> {
                client.search(new SearchRequest().indices("bulk").types("test"), new ActionListener<SearchResponse>() {
                    @Override
                    public void onResponse(SearchResponse searchResponse) {

                        ctx.verify(() -> assertThat(searchResponse.getHits().totalHits).isEqualTo(bulk));
                        ctx.completeNow();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ctx.failNow(e);
                    }
                });
            });
        });
        ctx.awaitCompletion(1, TimeUnit.MINUTES);
    }

    @AfterAll
    static void stopElasticSearch(Vertx vertx, VertxTestContext ctx) throws IOException {
        client.close();
        node.close();

        vertx.fileSystem().deleteRecursive("elasticsearch-data", true, ctx.succeeding(zoid -> ctx.completeNow()));
    }

    private static class MyNode extends Node {
        MyNode(Settings preparedSettings, Collection<Class<? extends Plugin>> classpathPlugins) {
            super(InternalSettingsPreparer.prepareEnvironment(preparedSettings, null), classpathPlugins);
        }
    }
}