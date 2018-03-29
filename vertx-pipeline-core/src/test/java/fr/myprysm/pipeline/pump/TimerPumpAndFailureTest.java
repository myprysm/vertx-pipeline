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

package fr.myprysm.pipeline.pump;

import fr.myprysm.pipeline.VertxTest;
import fr.myprysm.pipeline.validation.ValidationException;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TimerPump tests + Failure tests")
public class TimerPumpAndFailureTest implements VertxTest {

    public static final String TEST_TO = "test-timer-pump";
    public static final Long TEST_INTERVAL = 5L;
    public static final String TEST_UNIT = TimeUnit.MILLISECONDS.name();

    private static DeploymentOptions CONFIG = new DeploymentOptions()
            .setConfig(obj()
                    .put("to", arr().add(TEST_TO))
                    .put("name", "test")
                    .put("type", "fr.myprysm.pipeline.pump.TimerPump")
                    .put("interval", TEST_INTERVAL)
                    .put("unit", TEST_UNIT)
                    .put("data", obj().put("field", "value"))
            );

    @Test
    @DisplayName("TimerPump should emit values every 5ms")
    void timerEmitsValues(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        vertx.deployVerticle("fr.myprysm.pipeline.pump.TimerPump", CONFIG, ctx.succeeding(id -> {
            AtomicInteger count = new AtomicInteger();
            Checkpoint cp = ctx.strictCheckpoint(10);
            vertx.eventBus().<JsonObject>consumer(TEST_TO, message -> {
                // Undeploy after 10 mesages...
                if (count.getAndIncrement() == 10) vertx.undeploy(id);

                JsonObject data = message.body();
                assertThat(data.getLong("counter")).isNotNull().isGreaterThanOrEqualTo(0);
                assertThat(data.getLong("timestamp")).isNotNull();
                assertThat(data.getString("field")).isEqualTo("value");
                cp.flag();
            });
        }));

        ctx.awaitCompletion(1, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Configuration must be present and must be valid")
    void testPumpCannotRunWithoutConfiguration(Vertx vertx, VertxTestContext ctx) {
        vertx.deployVerticle("fr.myprysm.pipeline.pump.TimerPump", (id) -> {
            assertThat(id.failed()).isTrue();
            assertThat(id.cause()).isInstanceOf(ValidationException.class);
            ctx.completeNow();
        });
    }

    @Test
    @DisplayName("Validates that errors are not blocking the processor lifecycle")
    void testPumpFailures(Vertx vertx, VertxTestContext ctx) {
        vertx.deployVerticle(new FailurePump(), CONFIG, ctx.succeeding(id -> vertx.undeploy(id, ctx.succeeding(ar -> ctx.completeNow()))));
    }

    public static class FailurePump extends BaseJsonPump<PumpOptions> {

        @Override
        public Flowable<JsonObject> pump() {
            return Flowable.error(new IllegalArgumentException());
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
        public PumpOptions readConfiguration(JsonObject config) {
            return new PumpOptions(config);
        }

        @Override
        public Completable configure(PumpOptions config) {
            return Completable.complete();
        }

        @Override
        public ValidationResult validate(JsonObject config) {
            return ValidationResult.valid();
        }
    }
}
