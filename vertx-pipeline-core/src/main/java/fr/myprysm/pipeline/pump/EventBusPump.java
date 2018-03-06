package fr.myprysm.pipeline.pump;

import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;

public class EventBusPump extends BaseJsonPump<EventBusPumpOptions> {
    private String address;
    private MessageConsumer<JsonObject> consumer;

    @Override
    public Flowable<JsonObject> pump() {
        return consumer.toFlowable().map(this::read);
    }

    private JsonObject read(Message<JsonObject> message) {
        if (message != null && message.body() != null) {
            return message.body();
        }

        error("Got a null event!");
        return obj();
    }

    @Override
    protected Completable startVerticle() {
        consumer = eventBus().consumer(address);
        return Completable.complete();
    }

    @Override
    public EventBusPumpOptions readConfiguration(JsonObject config) {
        return new EventBusPumpOptions(config);
    }

    @Override
    public Completable configure(EventBusPumpOptions config) {
        address = config.getAddress();
        return Completable.complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return EventBusPumpOptionsValidation.validate(config);
    }
}
