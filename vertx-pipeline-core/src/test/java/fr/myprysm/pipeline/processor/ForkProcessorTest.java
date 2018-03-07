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
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class ForkProcessorTest implements VertxTest {

    private static final JsonObject MESSAGE = obj().put("foo", "bar");
    public static final String VERTICLE = "fr.myprysm.pipeline.processor.ForkProcessor";
    static final JsonObject CONFIG = obj()
            .put("name", "fork-processor-test")
            .put("type", VERTICLE)
            .put("from", "from")
            .put("to", arr().add("to"))
            .put("controlChannel", "channel")
            .put("publish", arr().add("publish"))
            .put("send", arr().add("send"));
    static DeploymentOptions OPTIONS = new DeploymentOptions().setConfig(CONFIG);


    @Test
    @DisplayName("Testing event bus publications with fork processor")
    void testForkProcessorPublications(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        Checkpoint cpPublish = ctx.strictCheckpoint(6);

        // 3 consumers on publish, 2 messages will validate the checkpoint with 6 ticks
        registerConsumer(vertx, "publish", cpPublish);
        registerConsumer(vertx, "publish", cpPublish);
        registerConsumer(vertx, "publish", cpPublish);


        Checkpoint cpSend = ctx.strictCheckpoint(2);
        // 2 consumers on send, 2 messages should validate the checkpoint
        registerConsumer(vertx, "send", cpSend);
        registerConsumer(vertx, "send", cpSend);

        Checkpoint cpChain = ctx.strictCheckpoint(2);
        registerConsumer(vertx, "to", cpChain);

        vertx.deployVerticle(VERTICLE, OPTIONS, ctx.succeeding(id -> {
            vertx.eventBus().send("from", MESSAGE);
            vertx.eventBus().send("from", MESSAGE);
        }));


        ctx.awaitCompletion(2, TimeUnit.SECONDS);
    }

    private void registerConsumer(Vertx vertx, String address, Checkpoint checkpoint) {
        vertx.<JsonObject>eventBus().consumer(address, message -> {
            assertThat(message.body()).isEqualTo(MESSAGE);
            checkpoint.flag();
        });
    }
}