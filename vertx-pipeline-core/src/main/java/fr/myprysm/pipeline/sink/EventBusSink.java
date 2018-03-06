package fr.myprysm.pipeline.sink;

import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static io.reactivex.Completable.complete;

/**
 * Sink that is able to forward the event to another address on the bus.
 * <p>
 * It can do both <code>publish</code> and <code>send</code> the event
 * to a list of addresses.
 */
public class EventBusSink extends BaseJsonSink<EventBusSinkOptions> {

    private List<String> publish;
    private List<String> send;

    @Override
    public void drain(JsonObject item) {
        for (String address : publish) {
            debug("Publishing to {}", address);
            eventBus().publish(address, item);
        }

        for (String address : send) {
            debug("Sending to {}", address);
            eventBus().send(address, item);
        }
    }

    @Override
    protected Completable startVerticle() {
        return complete();
    }

    @Override
    public EventBusSinkOptions readConfiguration(JsonObject config) {
        return new EventBusSinkOptions(config);
    }

    @Override
    public Completable configure(EventBusSinkOptions config) {
        publish = config.getPublish();
        send = config.getSend();
        return complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return EventBusSinkOptionsValidation.validate(config);
    }
}
