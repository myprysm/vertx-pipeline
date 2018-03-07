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
import fr.myprysm.pipeline.validation.ValidationException;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.validation.ValidationResult.valid;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessorTest implements VertxTest {

    public static final String TEST_FROM = "test-from";
    public static final String TEST_TO = "test-to";
    private static final String VERTICLE = "fr.myprysm.pipeline.processor.NoOpProcessor";
    private static DeploymentOptions CONFIG = new DeploymentOptions()
            .setConfig(new JsonObject()
                    .put("from", TEST_FROM)
                    .put("to", arr().add(TEST_TO))
                    .put("name", "test")
                    .put("type", "fr.myprysm.pipeline.processor.NoOpProcessor")
            );

    public static JsonObject DATA = new JsonObject().put("foo", "bar");
    public static JsonObject FAIL_DATA = new JsonObject().put("fail", true);
    public static JsonObject THROW_DATA = new JsonObject().put("throw", true);

    @Test
    @DisplayName("NoOp emits values as they come")
    void testNoOpProcessorEmitsItems(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        EventBus eb = vertx.eventBus();
        NoOpProcessor verticle = new NoOpProcessor();
        eb.<JsonObject>consumer(TEST_TO, (message) -> {
            ctx.verify(() -> {
                assertThat(verticle.from()).isEqualTo(TEST_FROM);
                assertThat(verticle.recipients()).containsExactly(TEST_TO);

                assertThat(message.body()).isEqualTo(DATA);
                ctx.completeNow();
            });

        });

        vertx.deployVerticle(verticle, CONFIG, ctx.succeeding((id) -> eb.send(TEST_FROM, DATA)));
        ctx.awaitCompletion(1, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Configuration must be present and must be valid")
    void testProcessorCannotRunWithoutConfiguration(Vertx vertx, VertxTestContext ctx) {
        vertx.deployVerticle(VERTICLE, (id) -> {
            assertThat(id.failed()).isTrue();
            assertThat(id.cause()).isInstanceOf(ValidationException.class);
            ctx.completeNow();
        });
    }

    @Test
    @DisplayName("Validates that errors are not blocking the processor lifecycle")
    void testProcessorFailures(Vertx vertx, VertxTestContext ctx) {
        vertx.eventBus().<JsonObject>consumer(TEST_TO, (message) -> {
            assertThat(message.body()).isEqualTo(DATA);
            ctx.completeNow();
        });
        vertx.deployVerticle(new FailureProcessor(), CONFIG, ctx.succeeding((id) -> {
            vertx.eventBus().send(TEST_FROM, FAIL_DATA);
            vertx.eventBus().send(TEST_FROM, THROW_DATA);
            vertx.eventBus().send(TEST_FROM, DATA);
        }));
    }

    static class FailureProcessor extends BaseJsonProcessor<ProcessorOptions> {

        @Override
        public Single<JsonObject> transform(JsonObject input) {
            if (input.equals(FAIL_DATA)) {
                return Single.error(new NullPointerException());
            } else if (input.equals(THROW_DATA)) {
                throw new NullPointerException();
            }
            return Single.just(input);
        }

        @Override
        public Completable shutdown() {
            return Completable.error(new NullPointerException());
        }

        @Override
        protected Completable startVerticle() {
            return Completable.complete();
        }

        @Override
        public ProcessorOptions readConfiguration(JsonObject config) {
            return new ProcessorOptions(config);
        }

        @Override
        public Completable configure(ProcessorOptions config) {
            return Completable.complete();
        }

        @Override
        public ValidationResult validate(JsonObject config) {
            return valid();
        }
    }
}
