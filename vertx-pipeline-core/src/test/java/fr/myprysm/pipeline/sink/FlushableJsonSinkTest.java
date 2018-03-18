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
import fr.myprysm.pipeline.util.Signal;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static fr.myprysm.pipeline.validation.ValidationResult.valid;
import static org.assertj.core.api.Assertions.assertThat;

class FlushableJsonSinkTest extends ConsoleTest implements VertxTest {

    @Test
    @DisplayName("Flushable sink handle signals")
    void testFlushableJsonSinkHandleSignals(Vertx vertx, VertxTestContext ctx) {
        TestFlushableSink verticle = new TestFlushableSink();
        DeploymentOptions options = new DeploymentOptions().setConfig(obj()
                .put("controlChannel", "channel")
                .put("name", "name")
                .put("type", "type")
                .put("from", "from"));

        vertx.deployVerticle(verticle, options,
                ctx.succeeding(id -> {
                    assertThat(verticle.batchSize()).isEqualTo(10);
                    vertx.eventBus().publish("channel", Signal.FLUSH.toString());
                    vertx.eventBus().publish("channel", Signal.FLUSH.toString());
                    vertx.setTimer(20, timer -> {
                        assertConsoleContainsPattern("IllegalArgumentException.*This is a test");
                        ctx.completeNow();
                    });
                }));
    }

    final class TestFlushableSink extends FlushableJsonSink<FlushableSinkOptions> {
        AtomicInteger count = new AtomicInteger();

        @Override
        public void drain(JsonObject item) {

        }

        @Override
        protected Completable startVerticle() {
            return Completable.complete();
        }

        @Override
        public FlushableSinkOptions readConfiguration(JsonObject config) {
            return new FlushableSinkOptions(config);
        }

        @Override
        public Completable configure(FlushableSinkOptions config) {
            return Completable.complete();
        }

        @Override
        public Integer batchSize() {
            return configuration().getBatchSize();
        }

        @Override
        public Completable flush() {
            return Completable.complete();
        }

        @Override
        public Completable onSignal(Signal signal) {
            if (count.incrementAndGet() % 2 == 0) {
                return Completable.error(new IllegalArgumentException("This is a test"));
            }
            return flush();
        }

        @Override
        public ValidationResult validate(JsonObject config) {
            return valid();
        }
    }

}