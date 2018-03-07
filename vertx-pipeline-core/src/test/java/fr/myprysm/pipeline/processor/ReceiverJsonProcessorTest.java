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
import fr.myprysm.pipeline.util.Signal;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class ReceiverJsonProcessorTest implements VertxTest {
    private static final String VERTICLE = "fr.myprysm.pipeline.processor.TestReceiverJsonProcessor";
    private static final JsonObject CONFIG = obj()
            .put("name", "receiver-processor-test")
            .put("type", VERTICLE)
            .put("from", "from")
            .put("to", arr().add("to"))
            .put("controlChannel", "channel");
    static DeploymentOptions OPTIONS = new DeploymentOptions().setConfig(CONFIG);

    @Test
    @DisplayName("Receiver processor should handle errors properly")
    void testThatReceiverProcessorHandlesErrorsProperlyOnSignal(Vertx vertx, VertxTestContext ctx) {
        TestReceiverJsonProcessor verticle = new TestReceiverJsonProcessor();
        vertx.deployVerticle(verticle, OPTIONS, ctx.succeeding(id -> {
            ctx.verify(() -> {
                assertThat(verticle.controlChannel()).isEqualTo("channel");

                vertx.eventBus().send("channel", "flush");
                vertx.eventBus().send("channel", "FLUSH");
                vertx.eventBus().send("channel", "TERMINATE");
                vertx.eventBus().send("channel", "UNRECOVERABLE");
                vertx.setTimer(50, timer -> ctx.completeNow());
            });
        }));
    }


    final class TestReceiverJsonProcessor extends ReceiverJsonProcessor<ProcessorOptions> {

        @Override
        public Single<JsonObject> transform(JsonObject input) {
            return Single.just(input);
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
        public Completable onSignal(Signal signal) {
            switch (signal) {
                case INTERRUPT:
                    throw new NullPointerException();
                case UNRECOVERABLE:
                    return Completable.error(new NullPointerException());
                case RESUME:
                default:
                    return Completable.complete();
            }
        }

        @Override
        public ValidationResult validate(JsonObject config) {
            return ValidationResult.valid();
        }
    }
}