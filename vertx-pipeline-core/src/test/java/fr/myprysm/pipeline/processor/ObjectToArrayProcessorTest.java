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
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class ObjectToArrayProcessorTest implements VertxTest {
    public static final String VERTICLE = "fr.myprysm.pipeline.processor.ObjectToArrayProcessor";
    static final JsonObject CONFIG = obj()
            .put("name", "object-to-array-test")
            .put("type", VERTICLE)
            .put("from", "from")
            .put("to", arr().add("to"))
            .put("fields", arr()
                    .add("first.path")
                    .add("non.existing.path")
                    .add("object")
            );
    static DeploymentOptions OPTIONS = new DeploymentOptions().setConfig(CONFIG);

    static final JsonObject INPUT = obj()
            .put("first", obj().put("path", "a secret"))
            .put("object", obj().put("path", "another secret"));

    @Test
    @DisplayName("Testing transformations with object to array")
    void testObjectToArrayProcessor(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        Checkpoint cp = ctx.checkpoint(10);
        vertx.eventBus().<JsonObject>consumer("to", message -> {
            cp.flag();
            JsonObject json = message.body();
            Optional<Object> opt = JsonHelpers.extractObject(json, "first.path");
            assertThat(opt).hasValue(arr().add("a secret"));

            opt = JsonHelpers.extractObject(json, "non.existing.path");
            assertThat(opt).hasValue(arr());

            opt = JsonHelpers.extractObject(json, "object");
            assertThat(opt).hasValue(arr().add(obj().put("path", "another secret")));

        });

        vertx.deployVerticle(VERTICLE, OPTIONS, ctx.succeeding(id -> {
            for (int i = 0, max = 15; i < max; i++) {
                vertx.eventBus().send("from", INPUT);
            }
        }));

        ctx.awaitCompletion(2, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("ObjectToArray does nothing when already an array")
    void testObjectToArrayDoesNothingWhenAlreadyAnArray(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        Checkpoint cp = ctx.checkpoint(10);
        vertx.eventBus().<JsonObject>consumer("to", message -> {
            cp.flag();
            JsonObject json = message.body();
            Optional<Object> opt = JsonHelpers.extractObject(json, "object");
            assertThat(opt).hasValue(arr().add(obj().put("foo", "bar")));

        });

        vertx.deployVerticle(VERTICLE, OPTIONS, ctx.succeeding(id -> {
            for (int i = 0, max = 15; i < max; i++) {
                vertx.eventBus().send("from", obj().put("object", arr().add(obj().put("foo", "bar"))));
            }
        }));

        ctx.awaitCompletion(2, TimeUnit.SECONDS);
    }


}