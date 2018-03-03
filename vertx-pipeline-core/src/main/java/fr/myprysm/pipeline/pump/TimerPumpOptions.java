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

package fr.myprysm.pipeline.pump;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;

@DataObject(generateConverter = true)
public class TimerPumpOptions extends PumpOptions {
    public static final Long DEFAULT_INTERVAL = 1000L;
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.MILLISECONDS;
    public static final JsonObject DEFAULT_DATA = obj();


    private Long interval = DEFAULT_INTERVAL;
    private TimeUnit unit = DEFAULT_UNIT;
    private JsonObject data = DEFAULT_DATA;

    public TimerPumpOptions() {

    }

    public TimerPumpOptions(TimerPumpOptions other) {
        super(other);
        interval = other.interval;
    }

    public TimerPumpOptions(PumpOptions other) {
        super(other);
    }

    public TimerPumpOptions(JsonObject json) {
        super(json);
        TimerPumpOptionsConverter.fromJson(json, this);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        TimerPumpOptionsConverter.toJson(this, json);
        return json;
    }

    /**
     * The interval of the pump.
     *
     * @return the interval of the pump.
     */
    public Long getInterval() {
        return interval;
    }

    /**
     * The interval of the pump.
     * <p>
     * The value must be a positive integer.
     * <p>
     * The pump will emit a message every tick containing the
     * current counter and timestamp.
     *
     * @param interval the interval to set.
     * @return this
     */
    public TimerPumpOptions setInterval(Long interval) {
        this.interval = interval;
        return this;
    }

    /**
     * The time unit of the pump.
     *
     * @return the time unit of the pump.
     */
    public TimeUnit getUnit() {
        return unit;
    }

    /**
     * The time unit of the pump.
     * <p>
     * The value is one of {@link TimeUnit}
     *
     * @param unit the time unit of the pump
     * @return this
     */
    public TimerPumpOptions setUnit(TimeUnit unit) {
        this.unit = unit;
        return this;
    }

    /**
     * The custom data to add with the tick
     *
     * @return the custom data to add with the tick
     */
    public JsonObject getData() {
        return data;
    }

    /**
     * The custom data to add with the tick
     * <p>
     * It can be any arbitrary json/yaml data.
     * <p>
     * No additional data is sent when it is null or empty
     *
     * @param data the additional data
     * @return this
     */
    public TimerPumpOptions setData(JsonObject data) {
        this.data = data;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public TimerPumpOptions setName(String name) {
        return (TimerPumpOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public TimerPumpOptions setType(String type) {
        return (TimerPumpOptions) super.setType(type);
    }

    @Override
    public String toString() {
        return "TimerPumpOptions{" +
                "interval=" + interval +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimerPumpOptions)) return false;
        if (!super.equals(o)) return false;
        TimerPumpOptions that = (TimerPumpOptions) o;
        return Objects.equals(interval, that.interval);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), interval);
    }
}
