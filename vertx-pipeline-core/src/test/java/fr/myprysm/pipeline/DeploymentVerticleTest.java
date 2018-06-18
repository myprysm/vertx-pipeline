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

package fr.myprysm.pipeline;

import fr.myprysm.pipeline.util.Signal;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.eventbus.EventBus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class DeploymentVerticleTest implements VertxTest {

    @Test
    @DisplayName("Deployment verticle starts successfully")
    void testDeploymentVerticle(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        vertx.deployVerticle("fr.myprysm.pipeline.DeploymentVerticle", ctx.succeeding(id -> {
            ctx.completeNow();
        }));
        ctx.awaitCompletion(10, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Deployment verticle shuts down itself when no remaining pipeline")
    void testDeploymentVerticleShutsDownVertxWhenRequested(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        DeploymentOptions options = new DeploymentOptions().setConfig(obj()
                .put("path", "deployment-should-shutdown.yml")
                .put("on.terminate.shutdown", false)
        );
        vertx.deployVerticle("fr.myprysm.pipeline.DeploymentVerticle", options, ctx.succeeding(id -> {
            vertx.setTimer(300, timer -> {
                ctx.verify(() -> {
                    assertThat(vertx.deploymentIDs()).doesNotContain(id);
                    ctx.completeNow();
                });

            });
        }));
        ctx.awaitCompletion(10, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Deployment verticle starts pipeline with aliased components")
    void testDeploymentVerticleStartsPipelineWithAliasedComponents(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        DeploymentOptions options = new DeploymentOptions().setConfig(obj()
                .put("path", "config-with-aliases.yml")
                .put("on.terminate.shutdown", false)
        );
        DeploymentVerticle verticle = new DeploymentVerticle();
        vertx.deployVerticle(verticle, options, ctx.succeeding(id -> {
            ctx.verify(() -> {
                assertThat(verticle.eventBus()).isInstanceOf(EventBus.class);
                assertThat(verticle.name()).isEqualTo(DeploymentVerticle.NAME + ':' + verticle.controlChannel());
                assertThat(verticle.exchange()).isNull();
                verticle.onSignal(Signal.INTERRUPT).test().assertComplete();
                ctx.completeNow();
            });
        }));
        ctx.awaitCompletion(10, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Deployment verticle cannot start pipeline with bad aliases")
    void testDeploymentVerticleCannotStartPipelineWithBadAliases(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        DeploymentOptions options = new DeploymentOptions().setConfig(obj()
                .put("path", "config-with-wrong-aliases.yml")
                .put("on.terminate.shutdown", false)
        );
        vertx.deployVerticle("fr.myprysm.pipeline.DeploymentVerticle", options, ctx.failing(t -> ctx.completeNow()));
        ctx.awaitCompletion(10, TimeUnit.SECONDS);
    }
}
