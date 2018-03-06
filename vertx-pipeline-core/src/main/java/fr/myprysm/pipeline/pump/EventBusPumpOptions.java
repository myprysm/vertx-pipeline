package fr.myprysm.pipeline.pump;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject(generateConverter = true)
public class EventBusPumpOptions extends PumpOptions {

    private String address;

    public EventBusPumpOptions() {

    }

    public EventBusPumpOptions(EventBusPumpOptions other) {
        super(other);
        address = other.address;
    }

    public EventBusPumpOptions(PumpOptions other) {
        super(other);
    }

    public EventBusPumpOptions(JsonObject json) {
        super(json);
        EventBusPumpOptionsConverter.fromJson(json, this);
    }

    /**
     * The address to listen on the event bus.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * The address to listen on the event bus.
     * <p>
     * The pump can only listen to one topic.
     *
     * @param address the address
     * @return this
     */
    public EventBusPumpOptions setAddress(String address) {
        this.address = address;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public EventBusPumpOptions setName(String name) {
        return (EventBusPumpOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public EventBusPumpOptions setType(String type) {
        return (EventBusPumpOptions) super.setType(type);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        EventBusPumpOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventBusPumpOptions)) return false;
        if (!super.equals(o)) return false;
        EventBusPumpOptions that = (EventBusPumpOptions) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), address);
    }

    @Override
    public String toString() {
        return "EventBusPumpOptions{" +
                "address='" + address + '\'' +
                '}';
    }
}
