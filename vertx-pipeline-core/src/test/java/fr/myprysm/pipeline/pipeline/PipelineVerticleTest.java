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

package fr.myprysm.pipeline.pipeline;

import fr.myprysm.pipeline.ConsoleTest;
import fr.myprysm.pipeline.VertxTest;
import fr.myprysm.pipeline.util.Signal;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

import java.util.concurrent.TimeUnit;

import static io.reactivex.Completable.complete;
import static org.assertj.core.api.Assertions.assertThat;

class PipelineVerticleTest extends ConsoleTest implements VertxTest {

    private static final String PIPELINE_VERTICLE = "fr.myprysm.pipeline.pipeline.PipelineVerticle";
    private static JsonObject config;

    @BeforeAll
    static void setUp(Vertx vertx, VertxTestContext ctx) {
        ConfigStoreOptions store = new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject().put("path", "test-config.yml"));
        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(store));
        retriever.getConfig(ctx.succeeding(json -> {
            config = json;
            ctx.completeNow();
        }));
    }

    @Test
    @DisplayName("PipelineVerticle is able to start simple pipeline")
    void testPipelineVerticleStartsSimpleFlow(Vertx vertx, VertxTestContext ctx) {
        DeploymentOptions options = getDeploymentOptions("simple");
        vertx.deployVerticle(PIPELINE_VERTICLE, options, ctx.succeeding(id ->
                vertx.setTimer(100L, timer -> vertx.undeploy(id, ctx.succeeding(v -> ctx.completeNow())))
        ));
    }

    @Test
    @DisplayName("PipelineVerticle is able to start multi instance / processors with file output in /tmp/output.json")
    void testPipelineVerticleStartsMultiInstanceMultiProcessorWithFileSink(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        DeploymentOptions options = getDeploymentOptions("multi-instance-multi-processor");
        PipelineVerticle verticle = new PipelineVerticle();
        vertx.deployVerticle(verticle, options, ctx.succeeding(id -> {
            ctx.verify(() -> {
                assertThat(verticle.controlChannel()).isNotBlank();
                assertThat(verticle.exchange()).isNull();
                verticle.emitSignal(Signal.UNRECOVERABLE); // Does nothing.
                assertThat(verticle.onSignal(Signal.UNRECOVERABLE)).isEqualTo(complete());

                vertx.undeploy(id, ctx.succeeding(v -> ctx.completeNow()));
            });
        }));

        ctx.awaitCompletion(5, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Logger processors....")
    void testLoggerProcessors(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        DeploymentOptions options = getDeploymentOptions("logger-test");

        vertx.deployVerticle(PIPELINE_VERTICLE, options, ctx.succeeding(id -> vertx.setTimer(100, timer -> {
            ctx.verify(() -> {
                for (Level level : Level.values()) {
                    assertConsoleContainsPattern(level.toString() + ".*LogProcessor.*log-processor");
                }
                ctx.completeNow();
            });
        })));

        ctx.awaitCompletion(5, TimeUnit.SECONDS);
    }


    @Test
    @DisplayName("Test timer shutdown signal triggers pipeline verticle signal.")
    void testTimerShutdown(Vertx vertx, VertxTestContext ctx) {
        DeploymentOptions options = getDeploymentOptions("timer-shutdown-test");
        vertx.eventBus().<String>consumer("test-shutdown-timer", message -> {
            ctx.verify(() -> {
                assertThat(message.headers().get("action")).isEqualTo(DeployChannelActions.UNDEPLOY.name());
                assertThat(message.body()).isEqualTo("timer-shutdown-test");
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(PIPELINE_VERTICLE, options, ctx.succeeding());

    }

    @Test
    @DisplayName("Test counter shutdown signal triggers pipeline verticle signal.")
    void testCounterShutdown(Vertx vertx, VertxTestContext ctx) {
        DeploymentOptions options = getDeploymentOptions("counter-shutdown-test");
        vertx.eventBus().<String>consumer("test-shutdown-counter", message -> {
            ctx.verify(() -> {
                assertThat(message.headers().get("action")).isEqualTo(DeployChannelActions.UNDEPLOY.name());
                assertThat(message.body()).isEqualTo("counter-shutdown-test");
                ctx.completeNow();
            });
        });

        vertx.deployVerticle(PIPELINE_VERTICLE, options, ctx.succeeding());

    }

    @Test
    @DisplayName("Pipeline verticle still stops when a component fails to undeploy.")
    void pipelineShouldStopWhenComponentFailsToUndeploy(Vertx vertx, VertxTestContext ctx) {
        DeploymentOptions options = getDeploymentOptions("undeploy-component-error");

        vertx.deployVerticle(PIPELINE_VERTICLE, options, ctx.succeeding((id) -> {
            vertx.undeploy(id, ctx.succeeding(z -> ctx.completeNow()));
        }));
    }

    private DeploymentOptions getDeploymentOptions(String pipeline) {
        return new DeploymentOptions().setConfig(config.getJsonObject(pipeline).put("name", pipeline));
    }

}