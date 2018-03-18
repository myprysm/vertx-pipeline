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
import io.vertx.core.*;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class JoltProcessorTest implements VertxTest {

    private static final String VERTICLE = "fr.myprysm.pipeline.processor.JoltProcessor";

    private static final JsonObject BASE_CONFIG = obj()
            .put("name", "jolt-processor")
            .put("type", VERTICLE)
            .put("from", "from")
            .put("to", arr().add("to"))
            .put("controlChannel", "channel");


    @Test
    @DisplayName("Jolt processor should transform basic JOLT sample from specs")
    void joltProcessorShouldTransformBasicSampleFromSpecs(Vertx vertx, VertxTestContext ctx) {
        testFromSpecs(vertx, ctx, "jolt-processor/jolt-sample.json");
    }

    @Test
    @DisplayName("Jolt processor should transform tweet from specs")
    void joltProcessorShouldTransformTweetFromSpecs(Vertx vertx, VertxTestContext ctx) {
        testFromSpecs(vertx, ctx, "jolt-processor/jolt-tweet.json");
    }

    @Test
    @DisplayName("Jolt processor should transform basic JOLT sample from yaml")
    void joltProcessorShouldTransformBasicSampleFromYaml(Vertx vertx, VertxTestContext ctx) {
        readTestSpecs(vertx, "jolt-processor/jolt-sample.json", ctx.succeeding(specs -> {
            MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
            consumer.handler(message -> consumer.unregister(zoid -> {

                ctx.verify(() -> assertThat(message.body()).isEqualTo(specs.expected));
                ctx.completeNow();
            }));
            JsonObject config = BASE_CONFIG.copy();
            DeploymentOptions opts = new DeploymentOptions().setConfig(config);
            config.put("path", "jolt-processor/jolt-sample.yaml")
                    .put("format", "yaml");
            vertx.deployVerticle(VERTICLE, opts, ctx.succeeding(id -> vertx.eventBus().send("from", specs.input)));
        }));
    }

    @Test
    @DisplayName("Jolt processor should fail to start with no valid config")
    void joltProcessorShouldNotStartWithoutValidConfig(Vertx vertx, VertxTestContext ctx) {
        readTestSpecs(vertx, "jolt-processor/jolt-sample.json", ctx.succeeding(specs -> {
            JsonObject config = BASE_CONFIG.copy();
            DeploymentOptions opts = new DeploymentOptions().setConfig(config);
            config.put("path", "jolt-processor/jolt-invalid.json");
            vertx.deployVerticle(VERTICLE, opts, ctx.failing(t -> ctx.completeNow()));
        }));
    }

    private void testFromSpecs(Vertx vertx, VertxTestContext ctx, String path) {
        readTestSpecs(vertx, path, ctx.succeeding(specs -> {
            MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("to");
            consumer.handler(message -> consumer.unregister(zoid -> {
                ctx.verify(() -> assertThat(message.body()).isEqualTo(specs.expected));
                ctx.completeNow();
            }));
            JsonObject config = BASE_CONFIG.copy();
            DeploymentOptions opts = new DeploymentOptions().setConfig(config);
            config.put("specs", specs.specs);
            vertx.deployVerticle(VERTICLE, opts, ctx.succeeding(id -> vertx.eventBus().send("from", specs.input)));
        }));
    }

    private void readTestSpecs(Vertx vertx, String path, Handler<AsyncResult<JoltTestSpecs>> handler) {

        vertx.<JsonObject>executeBlocking(f -> f.complete(objectFromFile(path)), specs -> {
            Future<JoltTestSpecs> future = Future.future();
            if (specs.failed()) {
                future.fail(specs.cause());
            } else if (obj().equals(specs.result())) {
                future.fail("Specs are empty");
            } else {
                future.complete(new JoltTestSpecs(specs.result()));
            }
            handler.handle(future);
        });

    }

    class JoltTestSpecs {
        JsonObject input;
        JsonArray specs;
        JsonObject expected;

        JoltTestSpecs(JsonObject config) {
            input = config.getJsonObject("input");
            specs = config.getJsonArray("specs");
            expected = config.getJsonObject("expected");
        }
    }

}