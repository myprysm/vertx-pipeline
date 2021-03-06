/*
 * Copyright 2018 the original author or the original authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.myprysm.pipeline.processor;

import fr.myprysm.pipeline.VertxTest;
import fr.myprysm.pipeline.util.Signal;
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

class TimerEmitterProcessorTest implements VertxTest {

    public static final String VERTICLE = "fr.myprysm.pipeline.processor.TimerEmitterProcessor";
    static final JsonObject CONFIG = obj()
            .put("name", "timer-emitter-test")
            .put("type", VERTICLE)
            .put("from", "from")
            .put("to", arr().add("to"))
            .put("controlChannel", "channel")
            .put("interval", 10L)
            .put("unit", "MILLISECONDS")
            .put("signal", "TERMINATE");
    static DeploymentOptions OPTIONS = new DeploymentOptions().setConfig(CONFIG);

    static final JsonObject INPUT = obj()
            .put("first", obj().put("path", "a secret"))
            .put("object", obj().put("path", "another secret"));

    @Test
    @DisplayName("Testing timer will emit 2 messages...")
    void testTimerEmitterProcessor(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        Checkpoint cp = ctx.checkpoint();
        vertx.eventBus().<String>consumer("channel", message -> {
            String signal = message.body();
            assertThat(signal).isIn(Signal.FLUSH.name(), Signal.TERMINATE.name());
            cp.flag();
        });

        vertx.deployVerticle(VERTICLE, OPTIONS, ctx.succeeding());

        ctx.awaitCompletion(2, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Testing timer will emit some FLUSH...")
    void testTimerFlush(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        Checkpoint cp = ctx.checkpoint(20);

        vertx.eventBus().<String>consumer("channel", message -> {
            String signal = message.body();
            assertThat(signal).isEqualTo(Signal.FLUSH.name());
            cp.flag();
        });

        DeploymentOptions opts = new DeploymentOptions().setConfig(CONFIG.copy().put("signal", "FLUSH"));
        vertx.deployVerticle(VERTICLE, opts, ctx.succeeding());

        ctx.awaitCompletion(2, TimeUnit.SECONDS);
    }


}