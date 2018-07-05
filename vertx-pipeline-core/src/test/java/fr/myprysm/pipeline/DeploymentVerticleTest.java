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

import fr.myprysm.pipeline.pipeline.PipelineService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class DeploymentVerticleTest implements VertxTest {

    @Test
    @DisplayName("Deployment verticle starts successfully")
    void itShouldStartSuccessfully(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        vertx.deployVerticle("fr.myprysm.pipeline.DeploymentVerticle", ctx.succeeding(id -> {
            ctx.completeNow();
        }));
        ctx.awaitCompletion(10, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Deployment verticle stops successfully")
    void itShouldStopSuccessfully(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        vertx.deployVerticle("fr.myprysm.pipeline.DeploymentVerticle", ctx.succeeding(id -> {
            vertx.undeploy(id, ctx.succeeding(zoid -> ctx.completeNow()));
        }));
        ctx.awaitCompletion(10, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Deployment verticle starts pipeline with aliased components")
    void itShouldStartPipelineWithAliasedComponents(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        DeploymentOptions options = new DeploymentOptions().setConfig(obj()
                .put("path", "config-with-aliases.yml")
        );
        DeploymentVerticle verticle = new DeploymentVerticle();
        vertx.deployVerticle(verticle, options, ctx.succeeding(id -> ctx.completeNow()));
        ctx.awaitCompletion(10, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Deployment verticle stays up when no pipeline is running")
    void itShouldStayUpWhenNoPipelineIsRunning(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        DeploymentOptions options = new DeploymentOptions().setConfig(obj()
                .put("path", "pipeline-should-shutdown.yml")
        );
        DeploymentVerticle verticle = new DeploymentVerticle();
        vertx.deployVerticle(verticle, options, ctx.succeeding(id -> {
            PipelineService proxy = new ServiceProxyBuilder(vertx).setAddress(PipelineService.ADDRESS).build(PipelineService.class);
            vertx.setTimer(100, timer -> {
                proxy.getRunningPipelines(ctx.succeeding(pipelines -> {
                    ctx.verify(() -> {
                        assertThat(pipelines.size()).isEqualTo(0);
                    });
                    ctx.completeNow();
                }));
            });


        }));
        ctx.awaitCompletion(10, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Deployment verticle cannot start pipeline with bad aliases")
    void itShouldNotStartPipelineWithBadAliases(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        DeploymentOptions options = new DeploymentOptions().setConfig(obj()
                .put("path", "config-with-wrong-aliases.yml")
        );
        vertx.deployVerticle("fr.myprysm.pipeline.DeploymentVerticle", options, ctx.failing(t -> ctx.completeNow()));
        ctx.awaitCompletion(10, TimeUnit.SECONDS);
    }
}
