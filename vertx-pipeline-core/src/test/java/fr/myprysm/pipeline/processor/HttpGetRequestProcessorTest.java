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

import fr.myprysm.pipeline.VertxTest;
import fr.myprysm.pipeline.util.JsonHelpers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class HttpGetRequestProcessorTest implements VertxTest {
    private static final Integer PORT = 8271;
    private static final String VERTICLE = "fr.myprysm.pipeline.processor.HttpGetRequestProcessor";

    private static final JsonObject CONFIG = obj()
            .put("name", "http-get-request-processor")
            .put("type", VERTICLE)
            .put("from", "from")
            .put("to", arr().add("to"))
            .put("controlChannel", "channel")
            .put("port", PORT)
            .put("injection", "another.place")
            .put("responseType", "OBJECT")
            .put("userAgent", "Vert.X Pipeline Test agent")
            .put("url", "/some/long/url/with/tick/:tick/timestamp/:timestamp/type/:type")
            .put("onError", "CONTINUE")
            .put("headers", obj()
                    .put("Accept", "application/json")
                    .put("X-Test-Tick", "$event.tick")
                    .put("X-Test-Timestamp", "$event.timestamp")
                    .put("X-Test-Type", "$event.type")
                    .put("X-Test-Value", "a value")
            )
            .put("queryParams", obj()
                    .put("foo", "bar")
                    .put("tick", "$event.tick")
                    .put("timestamp", "$event.timestamp")
                    .put("type", "$event.type")
            )
            .put("pathParams", obj()
                    .put("tick", "$event.tick")
                    .put("timestamp", "$event.timestamp")
                    .put("type", "$event.type")
                    .put("unmapped", "some unmapped path param")
            );
    static DeploymentOptions OPTIONS = new DeploymentOptions().setConfig(CONFIG);

    private static final int MESSAGE_COUNT = 10;

    @Test
    @DisplayName("HttpGetRequestProcessor can build a request and send it")
    void httpGetProcessorCanBuildARequestAndSendIt(Vertx vertx, VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint(MESSAGE_COUNT);
        vertx.eventBus().<JsonObject>consumer("to", message -> {
            JsonObject event = message.body();

            ctx.verify(() -> {
                // From headers
                assertThat(JsonHelpers.extractString(event, "another.place.headers.Accept")).hasValue("application/json");
                assertThat(JsonHelpers.extractString(event, "another.place.headers.X-Test-Value")).hasValue("a value");
                assertThat(JsonHelpers.extractString(event, "another.place.headers.X-Test-Type")).hasValue("some-type");
                assertThat(JsonHelpers.extractString(event, "another.place.headers.X-Test-Timestamp")).isNotEmpty();
                assertThat(JsonHelpers.extractString(event, "another.place.headers.X-Test-Tick")).isNotEmpty();

                // From query parameters
                assertThat(JsonHelpers.extractString(event, "another.place.queryParams.foo")).hasValue("bar");
                assertThat(JsonHelpers.extractString(event, "another.place.queryParams.type")).hasValue("some-type");
                assertThat(JsonHelpers.extractString(event, "another.place.queryParams.timestamp")).isNotEmpty();
                assertThat(JsonHelpers.extractString(event, "another.place.queryParams.tick")).isNotEmpty();

                // From path parameters
                assertThat(JsonHelpers.extractString(event, "another.place.pathParams.unmapped")).isEmpty();
                assertThat(JsonHelpers.extractString(event, "another.place.pathParams.type")).hasValue("some-type");
                assertThat(JsonHelpers.extractString(event, "another.place.pathParams.timestamp")).isNotEmpty();
                assertThat(JsonHelpers.extractString(event, "another.place.pathParams.tick")).isNotEmpty();
            });

            cp.flag();
        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, OPTIONS, ctx.succeeding(proc -> {
                for (int i = 0; i < MESSAGE_COUNT; i++) {
                    vertx.eventBus().send("from", obj()
                            .put("type", "some-type")
                            .put("timestamp", System.currentTimeMillis())
                            .put("tick", i)
                    );
                }
            }));
        }));
    }


    @Test
    @DisplayName("HttpGetRequestProcessor can continue when an error occurs")
    void httpGetProcessorCanContinueWhenAnErrorOccurs(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                assertThat(JsonHelpers.extractObject(event, "another.place")).isEmpty();
                ctx.completeNow();
                consumer.unregister(ctx.succeeding(z -> ctx.completeNow()));
            });

        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, OPTIONS, ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
            }));
        }));
    }

    @Test
    @DisplayName("HttpGetRequestProcessor discards invalid events")
    void httpGetProcessorDiscardsInvalidEvents(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                // From headers
                assertThat(JsonHelpers.extractString(event, "another.place.headers.Accept")).hasValue("application/json");
                assertThat(JsonHelpers.extractString(event, "another.place.headers.X-Test-Value")).hasValue("a value");
                assertThat(JsonHelpers.extractString(event, "another.place.headers.X-Test-Type")).hasValue("some-type");
                assertThat(JsonHelpers.extractString(event, "another.place.headers.X-Test-Timestamp")).isNotEmpty();
                assertThat(JsonHelpers.extractString(event, "another.place.headers.X-Test-Tick")).isNotEmpty();

                // From query parameters
                assertThat(JsonHelpers.extractString(event, "another.place.queryParams.foo")).hasValue("bar");
                assertThat(JsonHelpers.extractString(event, "another.place.queryParams.type")).hasValue("some-type");
                assertThat(JsonHelpers.extractString(event, "another.place.queryParams.timestamp")).isNotEmpty();
                assertThat(JsonHelpers.extractString(event, "another.place.queryParams.tick")).isNotEmpty();

                // From path parameters
                assertThat(JsonHelpers.extractString(event, "another.place.pathParams.unmapped")).isEmpty();
                assertThat(JsonHelpers.extractString(event, "another.place.pathParams.type")).hasValue("some-type");
                assertThat(JsonHelpers.extractString(event, "another.place.pathParams.timestamp")).isNotEmpty();
                assertThat(JsonHelpers.extractString(event, "another.place.pathParams.tick")).isNotEmpty();
                consumer.unregister(ctx.succeeding(z -> ctx.completeNow()));
            });

        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(CONFIG.copy().put("onError", "DISCARD")), ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
                vertx.eventBus().send("from", obj()
                        .put("type", "some-type")
                        .put("timestamp", System.currentTimeMillis())
                        .put("tick", 1)
                );
            }));
        }));
    }

    class WebServerVerticle extends AbstractVerticle {
        @Override
        public void start(Future<Void> startFuture) {
            HttpServer server = vertx.createHttpServer(new HttpServerOptions().setPort(PORT));
            Router router = Router.router(vertx);
            router.get("/some/long/url/with/tick/:tick/timestamp/:timestamp/type/:type")
                    .produces("application/json")
                    .handler(ctx -> {
                        JsonObject queryParams = obj();
                        JsonObject pathParams = obj();
                        JsonObject headers = obj();

                        JsonObject json = obj()
                                .put("queryParams", queryParams)
                                .put("pathParams", pathParams)
                                .put("headers", headers);

                        for (Map.Entry<String, String> pathParam : ctx.pathParams().entrySet()) {
                            pathParams.put(pathParam.getKey(), pathParam.getValue());
                        }

                        for (Map.Entry<String, String> queryParam : ctx.queryParams()) {
                            queryParams.put(queryParam.getKey(), queryParam.getValue());
                        }

                        for (Map.Entry<String, String> header : ctx.request().headers()) {
                            headers.put(header.getKey(), header.getValue());
                        }

                        ctx.response().end(json.toBuffer());
                    });

            server.requestHandler(router::accept).listen(started -> {
                if (started.succeeded()) {
                    startFuture.complete();
                } else {
                    startFuture.fail(started.cause());
                }
            });
        }
    }
}