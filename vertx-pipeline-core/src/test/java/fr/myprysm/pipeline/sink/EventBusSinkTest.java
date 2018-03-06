package fr.myprysm.pipeline.sink;

import fr.myprysm.pipeline.VertxTest;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class EventBusSinkTest implements VertxTest {

    private static final JsonObject MESSAGE = obj().put("foo", "bar");
    public static final String VERTICLE = "fr.myprysm.pipeline.sink.EventBusSink";
    static final JsonObject CONFIG = obj()
            .put("name", "eventbus-sink-test")
            .put("type", VERTICLE)
            .put("from", "from")
            .put("controlChannel", "channel")
            .put("publish", arr().add("publish"))
            .put("send", arr().add("send"));
    static DeploymentOptions OPTIONS = new DeploymentOptions().setConfig(CONFIG);


    @Test
    @DisplayName("Testing event bus publications with event bus sink")
    void testEventBusSinkPublications(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        Checkpoint cpPublish = ctx.checkpoint(6);

        // 3 consumers on publish, 2 messages will validate the checkpoint with 6 ticks
        registerConsumer(vertx, "publish", cpPublish);
        registerConsumer(vertx, "publish", cpPublish);
        registerConsumer(vertx, "publish", cpPublish);

        Checkpoint cpSend = ctx.checkpoint(2);

        // 2 consumers on send, 2 messages should validate the checkpoint
        registerConsumer(vertx, "send", cpSend);
        registerConsumer(vertx, "send", cpSend);

        vertx.deployVerticle(VERTICLE, OPTIONS, ctx.succeeding(id -> {
            vertx.eventBus().send("from", MESSAGE);
            vertx.eventBus().send("from", MESSAGE);
        }));


        ctx.awaitCompletion(2, TimeUnit.SECONDS);
    }

    private void registerConsumer(Vertx vertx, String address, Checkpoint checkpoint) {
        vertx.<JsonObject>eventBus().consumer(address, message -> {
            assertThat(message.body()).isEqualTo(MESSAGE);
            checkpoint.flag();
        });
    }
}