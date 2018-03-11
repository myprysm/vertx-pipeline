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
import io.vertx.core.buffer.Buffer;
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

@SuppressWarnings("Duplicates")
class HttpGetRequestProcessorTest implements VertxTest {
    private static final Integer PORT = 8271;
    private static final String VERTICLE = "fr.myprysm.pipeline.processor.HttpGetRequestProcessor";

    private static final JsonObject BASE_CONFIG = obj()
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
            .put("onError", "CONTINUE");

    private static final JsonObject CONFIG = BASE_CONFIG.copy()
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

    @Test
    @DisplayName("HttpGetRequestProcessor get properly a list")
    void httpGetProcessorGetAList(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                assertThat(JsonHelpers.extractJsonArray(event, "another.place")).hasValue(arr().add(obj().put("key1", "value1")).add(obj().put("key2", "value2")));
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(BASE_CONFIG.copy().put("url", "/list").put("responseType", "LIST")), ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
            }));
        }));
    }

    @Test
    @DisplayName("HttpGetRequestProcessor fails when not a list")
    void httpGetProcessorThrowsErrorWhenNotAList(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                assertThat(JsonHelpers.extractString(event, "another.place")).isEmpty();
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(BASE_CONFIG.copy().put("url", "/string").put("responseType", "LIST")), ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
            }));
        }));
    }


    @Test
    @DisplayName("HttpGetRequestProcessor get properly an object")
    void httpGetProcessorGetAnObject(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                assertThat(JsonHelpers.extractJsonObject(event, "another.place")).hasValue(obj().put("foo", "bar"));
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(BASE_CONFIG.copy().put("url", "/object").put("responseType", "OBJECT")), ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
            }));
        }));
    }

    @Test
    @DisplayName("HttpGetRequestProcessor fails to extract an object when it is not")
    void httpGetProcessorThrowsErrorWhenItemIsNotAnObject(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                assertThat(JsonHelpers.extractString(event, "another.place")).isEmpty();
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(BASE_CONFIG.copy().put("url", "/string").put("responseType", "OBJECT")), ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
            }));
        }));
    }

    @Test
    @DisplayName("HttpGetRequestProcessor get properly the first item of a list")
    void httpGetProcessorGetTheFirstItemOfAList(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                assertThat(JsonHelpers.extractJsonObject(event, "another.place")).hasValue(obj().put("key1", "value1"));
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(BASE_CONFIG.copy().put("url", "/list").put("responseType", "LIST_EXTRACT_FIRST")), ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
            }));
        }));
    }

    @Test
    @DisplayName("HttpGetRequestProcessor fails to extract first item when response is not a list")
    void httpGetProcessorThrowsErrorWhenNotAListOfItem(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                assertThat(JsonHelpers.extractString(event, "another.place")).isEmpty();
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(BASE_CONFIG.copy().put("url", "/string").put("responseType", "LIST_EXTRACT_FIRST")), ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
            }));
        }));
    }

    @Test
    @DisplayName("HttpGetRequestProcessor get properly a double")
    void httpGetProcessorGetADouble(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                assertThat(JsonHelpers.extractDouble(event, "another.place")).hasValue(10.12D);
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(BASE_CONFIG.copy().put("url", "/double").put("responseType", "DOUBLE")), ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
            }));
        }));
    }

    @Test
    @DisplayName("HttpGetRequestProcessor fails when not a double")
    void httpGetProcessorThrowsErrorWhenNotADouble(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                assertThat(JsonHelpers.extractString(event, "another.place")).isEmpty();
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(BASE_CONFIG.copy().put("url", "/string").put("responseType", "DOUBLE")), ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
            }));
        }));
    }

    @Test
    @DisplayName("HttpGetRequestProcessor get properly a long")
    void httpGetProcessorGetALong(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                assertThat(JsonHelpers.extractLong(event, "another.place")).hasValue(10L);
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(BASE_CONFIG.copy().put("url", "/long").put("responseType", "LONG")), ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
            }));
        }));
    }

    @Test
    @DisplayName("HttpGetRequestProcessor fails when not a long")
    void httpGetProcessorThrowsErrorWhenNotALong(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                assertThat(JsonHelpers.extractString(event, "another.place")).isEmpty();
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(BASE_CONFIG.copy().put("url", "/string").put("responseType", "LONG")), ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
            }));
        }));
    }

    @Test
    @DisplayName("HttpGetRequestProcessor get properly a string")
    void httpGetProcessorGetAString(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                assertThat(JsonHelpers.extractString(event, "another.place")).hasValue("some string");
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(BASE_CONFIG.copy().put("url", "/string").put("responseType", "STRING")), ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
            }));
        }));
    }

    @Test
    @DisplayName("HttpGetRequestProcessor fails when status is KO")
    void httpGetProcessorFailsWhenStatusKO(Vertx vertx, VertxTestContext ctx) {
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
        consumer.handler(message -> {
            JsonObject event = message.body();
            ctx.verify(() -> {
                assertThat(JsonHelpers.extractString(event, "another.place")).isEmpty();
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(new WebServerVerticle(), ctx.succeeding(web -> {
            vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(BASE_CONFIG.copy().put("url", "/blahbla").put("responseType", "STRING")), ctx.succeeding(proc -> {
                vertx.eventBus().send("from", obj());
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

            router.get("/object")
                    .produces("application/json")
                    .handler(ctx -> ctx.response().end(obj().put("foo", "bar").toBuffer()));


            router.get("/list")
                    .produces("application/json")
                    .handler(ctx -> ctx.response().end(arr().add(obj().put("key1", "value1")).add(obj().put("key2", "value2")).toBuffer()));

            router.get("/double")
                    .handler(ctx -> ctx.response().end(Buffer.buffer(String.valueOf(10.12D))));

            router.get("/long")
                    .handler(ctx -> ctx.response().end(Buffer.buffer(String.valueOf(10L))));

            router.get("/string")
                    .handler(ctx -> ctx.response().end(Buffer.buffer("some string")));

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