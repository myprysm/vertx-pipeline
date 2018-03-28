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
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Cron pump tests")
class CronPumpTest implements VertxTest {
    public static final String TEST_TO = "test-cron-pump";
    public static final String TEST_CRON = "*/1 * * * * ?";
    private static final String VERTICLE = "fr.myprysm.pipeline.pump.CronPump";

    private static final JsonObject CONFIG = obj()
            .put("to", arr().add(TEST_TO))
            .put("name", "test")
            .put("type", VERTICLE)
            .put("cron", TEST_CRON)
            .put("data", obj().put("field", "value"));
    private static DeploymentOptions OPTIONS = new DeploymentOptions()
            .setConfig(CONFIG);

    @Test
    @DisplayName("CronPump should emit values each second")
    void cronEmitsValues(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        vertx.deployVerticle(VERTICLE, OPTIONS, ctx.succeeding(id -> {
            vertx.eventBus().<JsonObject>consumer(TEST_TO, message -> {
                ctx.verify(() -> {
                    JsonObject data = message.body();
                    assertThat(data.getLong("counter")).isNotNull().isGreaterThanOrEqualTo(0);
                    assertThat(data.getLong("timestamp")).isNotNull();
                    assertThat(data.getString("field")).isEqualTo("value");

                });
                ctx.completeNow();
            });
        }));
    }

    @Test
    @DisplayName("CronPump should not start without a valid cron expression")
    void cronCannotStartWithoutValidExpression(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        JsonObject invalidConfig = CONFIG.copy().put("cron", "some invalid cron");
        vertx.deployVerticle(VERTICLE, new DeploymentOptions().setConfig(invalidConfig), ctx.failing(t -> ctx.completeNow()));
    }
}