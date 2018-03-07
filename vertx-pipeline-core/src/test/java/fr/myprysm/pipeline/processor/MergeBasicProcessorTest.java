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
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static fr.myprysm.pipeline.util.JsonHelpers.*;
import static org.assertj.core.api.Assertions.assertThat;

class MergeBasicProcessorTest implements VertxTest {
    private static final String VERTICLE = "fr.myprysm.pipeline.processor.MergeBasicProcessor";
    private static final JsonObject CONFIG = obj()
            .put("name", "merge-basic-test")
            .put("type", VERTICLE)
            .put("from", "from")
            .put("to", arr().add("to"))
            .put("controlChannel", "channel")
            .put("operations", obj()
                    .put("objToKey", "nested.field")
                    .put("mergeArrays", "some.array")
                    .put("sortArray", obj()
                            .put("path", "some.array")
                            .put("field", "counter")
                            .put("order", "DESC")
                            .put("type", "long")
                    )
            )
            .put("onFlush", obj()
                    .put("sort", obj()
                            .put("path", "some.string")
                            .put("type", "string")
                    )
            );
    static DeploymentOptions OPTIONS = new DeploymentOptions().setConfig(CONFIG);

    @Test
    @DisplayName("Testing batch size")
    void testBatchSize(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        JsonObject message1 = new JsonObject("{\"nested\":{\"field\":\"foo\"},\"some\":{\"array\":[{\"counter\":1},{\"counter\":2},{\"counter\":3}],\"string\":\"first string\"}}");
        JsonObject message2 = new JsonObject("{\"nested\":{\"field\":\"bar\"},\"some\":{\"array\":[{\"counter\":2},{\"counter\":4},{\"counter\":5}],\"string\":\"second string\"}}");
        MergeBasicProcessor verticle = new MergeBasicProcessor();

        assertThat(verticle.batchSize()).isEqualTo(0);
        vertx.deployVerticle(verticle, OPTIONS, ctx.succeeding(id -> {
            vertx.eventBus()
                    .send("from", message1)
                    .send("from", message2);
            vertx.eventBus().send("channel", "RESUME");

            // Wait a bit before checking to let the time for data to be processed.
            vertx.setTimer(150L, timer -> {
                assertThat(verticle.batchSize()).isEqualTo(2);
                ctx.completeNow();
            });

        }));
    }

    @Test
    @DisplayName("Testing merge and sort longs on input array")
    void testMergeBasicWithLongs(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        JsonObject message1 = new JsonObject("{\"nested\":{\"field\":\"foo\"},\"some\":{\"array\":[{\"counter\":1},{\"counter\":2},{\"counter\":3}],\"string\":\"first string\"}}");
        JsonObject message2 = new JsonObject("{\"nested\":{\"field\":\"foo\"},\"some\":{\"array\":[{\"counter\":2},{\"counter\":4},{\"counter\":5}],\"string\":\"second string\"}}");

        vertx.eventBus().<JsonObject>consumer("to", message -> {
            JsonObject json = message.body();
            JsonArray arr = json.getJsonArray("merged");
            assertThat(arr).isNotNull();
            assertThat(arr).isInstanceOf(JsonArray.class);
            assertThat(arr).hasSize(1);
            JsonObject value = arr.getJsonObject(0);

            Optional<JsonArray> valueArray = JsonHelpers.extractJsonArray(value, "some.array");
            assertThat(valueArray).hasValue(arr()
                    .add(obj().put("counter", 5))
                    .add(obj().put("counter", 4))
                    .add(obj().put("counter", 3))
                    .add(obj().put("counter", 2))
                    .add(obj().put("counter", 1))
            );
            assertThat(JsonHelpers.extractString(value, "some.string")).hasValue("first string");
            ctx.completeNow();
        });

        vertx.deployVerticle(VERTICLE, OPTIONS, ctx.succeeding(id -> {
            vertx.eventBus()
                    .send("from", message1)
                    .send("from", message2);

            // Wait a bit before sending flush so that we ensure items are processed.
            vertx.setTimer(250L, timer -> vertx.eventBus().send("channel", "FLUSH"));

        }));
    }

    @Test
    @DisplayName("Testing merge and sort doubles on input array")
    void testMergeBasicWithDoubles(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        JsonObject message1 = new JsonObject("{\"nested\":{\"field\":\"foo\"},\"some\":{\"array\":[{\"counter\":1.1},{\"counter\":2.2},{\"counter\":3.3}],\"string\":\"first string\"}}");
        JsonObject message2 = new JsonObject("{\"nested\":{\"field\":\"foo\"},\"some\":{\"array\":[{\"counter\":2.3},{\"counter\":4.2},{\"counter\":5.1}],\"string\":\"second string\"}}");

        JsonObject confDouble = CONFIG.copy();
        writeObject(confDouble, "operations.sortArray.order", "ASC");
        writeObject(confDouble, "operations.sortArray.type", "double");

        vertx.eventBus().<JsonObject>consumer("to", message -> {
            JsonObject json = message.body();
            JsonArray arr = json.getJsonArray("merged");
            assertThat(arr).isNotNull();
            assertThat(arr).isInstanceOf(JsonArray.class);
            assertThat(arr).hasSize(1);
            JsonObject value = arr.getJsonObject(0);

            Optional<JsonArray> valueArray = JsonHelpers.extractJsonArray(value, "some.array");
            assertThat(valueArray).hasValue(arr()
                    .add(obj().put("counter", 1.1))
                    .add(obj().put("counter", 2.2))
                    .add(obj().put("counter", 2.3))
                    .add(obj().put("counter", 3.3))
                    .add(obj().put("counter", 4.2))
                    .add(obj().put("counter", 5.1))
            );
            assertThat(JsonHelpers.extractString(value, "some.string")).hasValue("first string");
            ctx.completeNow();
        });

        vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(confDouble), ctx.succeeding(id -> {
            vertx.eventBus()
                    .send("from", message1)
                    .send("from", message2);

            // Wait a bit before sending flush so that we ensure items are processed.
            vertx.setTimer(250L, timer -> vertx.eventBus().send("channel", "FLUSH"));

        }));
    }

    @Test
    @DisplayName("Testing merge with sorted output")
    void testMergeBasicSortsOutput(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        JsonObject message1 = new JsonObject("{\"nested\":{\"field\":\"foo\"},\"some\":{\"array\":[{\"counter\":1},{\"counter\":2},{\"counter\":3}],\"string\":\"first string\"}}");
        JsonObject message2 = new JsonObject("{\"nested\":{\"field\":\"bar\"},\"some\":{\"array\":[{\"counter\":2},{\"counter\":4},{\"counter\":5}],\"string\":\"second string\"}}");

        vertx.eventBus().<JsonObject>consumer("to", message -> {
            JsonObject json = message.body();
            JsonArray arr = json.getJsonArray("merged");
            assertThat(arr).isNotNull();
            assertThat(arr).isInstanceOf(JsonArray.class);
            assertThat(arr).hasSize(2);

            JsonObject value = arr.getJsonObject(0);
            Optional<JsonArray> valueArray = JsonHelpers.extractJsonArray(value, "some.array");
            assertThat(valueArray).hasValue(arr()
                    .add(obj().put("counter", 3))
                    .add(obj().put("counter", 2))
                    .add(obj().put("counter", 1))
            );
            assertThat(JsonHelpers.extractString(value, "some.string")).hasValue("first string");


            value = arr.getJsonObject(1);
            valueArray = JsonHelpers.extractJsonArray(value, "some.array");
            assertThat(valueArray).hasValue(arr()
                    .add(obj().put("counter", 5))
                    .add(obj().put("counter", 4))
                    .add(obj().put("counter", 2))
            );
            assertThat(JsonHelpers.extractString(value, "some.string")).hasValue("second string");

            ctx.completeNow();
        });

        vertx.deployVerticle(VERTICLE, OPTIONS, ctx.succeeding(id -> {
            vertx.eventBus()
                    .send("from", message2)
                    .send("from", message1);

            // Wait a bit before sending flush so that we ensure items are processed.
            vertx.setTimer(250L, timer -> vertx.eventBus().send("channel", "FLUSH"));

        }));
    }
}