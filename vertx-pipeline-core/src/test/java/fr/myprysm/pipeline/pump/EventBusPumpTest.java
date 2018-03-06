package fr.myprysm.pipeline.pump;

import fr.myprysm.pipeline.VertxTest;
import fr.myprysm.pipeline.pipeline.ExchangeOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class EventBusPumpTest implements VertxTest {
    private static final String VERTICLE = "fr.myprysm.pipeline.pump.EventBusPump";
    private static final JsonObject CONFIG = obj()
            .put("name", "eventbus-pump-test")
            .put("type", VERTICLE)
            .put("to", arr().add("to"))
            .put("controlChannel", "channel")
            .put("address", "address");
    static DeploymentOptions OPTIONS = new DeploymentOptions().setConfig(CONFIG);

    @Test
    @DisplayName("Event Bus pump emits message received.")
    void testThatEventBusPumpEmitsMessages(Vertx vertx, VertxTestContext ctx) {
        EventBusPump verticle = new EventBusPump();
        Checkpoint cp = ctx.checkpoint();
        JsonObject data = obj().put("foo", "bar");

        vertx.eventBus().<JsonObject>consumer("to", message -> {
            assertThat(message.body()).isEqualTo(data);
            cp.flag();
        });

        vertx.deployVerticle(verticle, OPTIONS, ctx.succeeding(id -> {
            assertThat(verticle.exchange()).isEqualTo(new ExchangeOptions(CONFIG));
            assertThat(verticle.delegate().getName()).isEqualTo("fr.myprysm.pipeline.pump.AbstractPump");
            assertThat(verticle.recipients()).containsExactly("to");

            for (int i = 0, max = 10; i < max; i++) {
                vertx.eventBus().send("address", data);
            }
        }));
    }

    @Test
    @DisplayName("Event Bus pump emits new object when null received.")
    void testThatEventBusPumpEmitsNewObjects(Vertx vertx, VertxTestContext ctx) {
        EventBusPump verticle = new EventBusPump();

        vertx.eventBus().<JsonObject>consumer("to", message -> {
            assertThat(message.body()).isEqualTo(obj());
            ctx.completeNow();
        });

        vertx.deployVerticle(verticle, OPTIONS, ctx.succeeding(id -> {
            vertx.eventBus().send("address", null);
        }));
    }

}