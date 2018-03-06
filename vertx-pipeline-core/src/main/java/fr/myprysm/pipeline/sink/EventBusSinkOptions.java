package fr.myprysm.pipeline.sink;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@DataObject(generateConverter = true)
public class EventBusSinkOptions extends SinkOptions {

    private List<String> publish = new ArrayList<>();
    private List<String> send = new ArrayList<>();

    public EventBusSinkOptions() {

    }

    public EventBusSinkOptions(EventBusSinkOptions other) {
        super(other);
        publish = other.publish;
        send = other.send;
    }

    public EventBusSinkOptions(SinkOptions other) {
        super(other);
    }

    public EventBusSinkOptions(JsonObject json) {
        super(json);
        EventBusSinkOptionsConverter.fromJson(json, this);
    }

    /**
     * Publication addresses.
     *
     * @return the list of addresses to publish
     */
    public List<String> getPublish() {
        return publish;
    }

    /**
     * Publication addresses.
     * <p>
     * Those publications are broadcasted to each registered consumer.
     *
     * @param publish the list of addresses to publish
     * @return this
     */
    public EventBusSinkOptions setPublish(List<String> publish) {
        this.publish = publish;
        return this;
    }

    /**
     * Publication addresses.
     *
     * @return the list of addresses to send
     */
    public List<String> getSend() {
        return send;
    }

    /**
     * Publication addresses.
     * <p>
     * Those publications are casted to the first registered consumer that get the message.
     *
     * @param send the list of addresses to send
     * @return this
     */
    public EventBusSinkOptions setSend(List<String> send) {
        this.send = send;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public EventBusSinkOptions setName(String name) {
        return (EventBusSinkOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public EventBusSinkOptions setType(String type) {
        return (EventBusSinkOptions) super.setType(type);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        EventBusSinkOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventBusSinkOptions)) return false;
        if (!super.equals(o)) return false;
        EventBusSinkOptions that = (EventBusSinkOptions) o;
        return Objects.equals(publish, that.publish) &&
                Objects.equals(send, that.send);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), publish, send);
    }

    @Override
    public String toString() {
        return "EventBusSinkOptions{" +
                "publish=" + publish +
                ", send=" + send +
                '}';
    }
}
