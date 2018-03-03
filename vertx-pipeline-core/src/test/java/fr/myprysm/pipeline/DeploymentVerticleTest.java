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

package fr.myprysm.pipeline;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeploymentVerticleTest implements VertxTest {

    private WebClient client;

    @BeforeEach
    public void setup(Vertx vertx, VertxTestContext ctx) {
        client = WebClient.create(vertx, new WebClientOptions()
                .setDefaultHost("localhost")
                .setDefaultPort(8080));
        vertx.deployVerticle("fr.myprysm.pipeline.DeploymentVerticle", ctx.succeeding(ar -> ctx.completeNow()));
    }


    @Disabled("No http server anymore in the verticle, waiting for new test writing.")
    @Test
    public void testHttpServer(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        client.get("/")
                .as(BodyCodec.string())
                .send((ar) -> {
                    if (ar.succeeded()) {
                        HttpResponse<String> resp = ar.result();
                        assertThat(resp.body()).isEqualTo("Hello from Vert.x!");
                        ctx.completeNow();
                    } else {
                        ctx.failNow(ar.cause());
                    }
                });
    }
}
