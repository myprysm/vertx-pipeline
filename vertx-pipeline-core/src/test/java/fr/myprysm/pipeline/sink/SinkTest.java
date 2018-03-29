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

package fr.myprysm.pipeline.sink;

import fr.myprysm.pipeline.ConsoleTest;
import fr.myprysm.pipeline.VertxTest;
import fr.myprysm.pipeline.pipeline.ExchangeOptions;
import fr.myprysm.pipeline.validation.ValidationException;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static org.assertj.core.api.Assertions.assertThat;

public class SinkTest extends ConsoleTest implements VertxTest {

    public static final String TEST_FROM = "test-console-sink";
    public static final String PATTERN_CONSOLE_OUTPUT = "^\\{\"foo\":\"bar\"}$";

    public static JsonObject DATA = new JsonObject().put("foo", "bar");
    public static JsonObject FAIL_DATA = new JsonObject().put("fail", true);

    private static DeploymentOptions CONFIG = new DeploymentOptions()
            .setConfig(new JsonObject()
                    .put("from", TEST_FROM)
                    .put("to", arr().add("to"))
                    .put("controlChannel", "controlChannel")
                    .put("name", "test")
                    .put("type", "fr.myprysm.pipeline.sink.ConsoleSink")
            );

    @Test
    @DisplayName("ConsoleSink should write items to System.out")
    void testConsoleSink(Vertx vertx, VertxTestContext ctx) {
        vertx.deployVerticle("fr.myprysm.pipeline.sink.ConsoleSink", CONFIG, ctx.succeeding(id -> {
            vertx.eventBus().send(TEST_FROM, DATA);
            vertx.setTimer(100, timer -> {
                ctx.verify(() -> assertConsoleContainsPattern(PATTERN_CONSOLE_OUTPUT));
                ctx.completeNow();
            });
        }));

    }

    @Test
    @DisplayName("Configuration must be present and must be valid")
    void testSinkCannotRunWithoutConfiguration(Vertx vertx, VertxTestContext ctx) {
        vertx.deployVerticle("fr.myprysm.pipeline.sink.ConsoleSink", ctx.failing(error -> {
            ctx.verify(() -> assertThat(error).isInstanceOf(ValidationException.class));
            ctx.completeNow();
        }));
    }

    @Test
    @DisplayName("Validates that errors are not blocking the processor lifecycle")
    void testSinkFailures(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        vertx.deployVerticle(new FailureSink(), CONFIG, (id) -> {

            vertx.eventBus().send(TEST_FROM, FAIL_DATA);
            vertx.eventBus().send(TEST_FROM, DATA);
            vertx.eventBus().send(TEST_FROM, null);
            vertx.setTimer(100, timer -> {
                ctx.verify(() -> assertConsoleContainsPattern(PATTERN_CONSOLE_OUTPUT));
                ctx.completeNow();
            });
        });

        ctx.awaitCompletion(5, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Validates that blackhole sink does not write anything in console output")
    void testBlackHoleSink(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        BlackholeSink blackHoleSink = new BlackholeSink();
        vertx.deployVerticle(blackHoleSink, CONFIG, (id) -> {
            // Verticle is started, thus config is written and we can test exchange options
            assertThat(blackHoleSink.name()).isEqualTo("test");
            ExchangeOptions exchange = blackHoleSink.exchange();
            assertThat(exchange.getFrom()).isEqualTo(TEST_FROM);
            assertThat(exchange.getControlChannel()).isEqualTo("controlChannel");
            assertThat(exchange.getTo()).containsExactly("to");

            vertx.eventBus().send(TEST_FROM, FAIL_DATA);
            vertx.eventBus().send(TEST_FROM, DATA);
            vertx.setTimer(100, timer -> {
                ctx.verify(() -> assertConsoleDoesNotContainPattern(PATTERN_CONSOLE_OUTPUT));
                ctx.completeNow();
            });
        });

        ctx.awaitCompletion(5, TimeUnit.SECONDS);
    }


    static class FailureSink extends BaseJsonSink<SinkOptions> {

        @Override
        public void drain(JsonObject item) {
            if (item.equals(FAIL_DATA)) throw new IllegalArgumentException();
            System.out.println(item.toString());
        }

        @Override
        public Completable shutdown() {
            return Completable.error(new IllegalArgumentException());
        }

        @Override
        protected Completable startVerticle() {
            return Completable.complete();
        }

        @Override
        public SinkOptions readConfiguration(JsonObject config) {
            return new SinkOptions(config);
        }

        @Override
        public Completable configure(SinkOptions config) {
            return Completable.complete();
        }

        @Override
        public ValidationResult validate(JsonObject config) {
            return ValidationResult.valid();
        }
    }
}
