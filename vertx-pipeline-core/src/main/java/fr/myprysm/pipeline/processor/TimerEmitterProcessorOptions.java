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

package fr.myprysm.pipeline.processor;

import fr.myprysm.pipeline.util.Signal;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@DataObject(generateConverter = true)
public class TimerEmitterProcessorOptions extends ProcessorOptions {

    public static final Long DEFAULT_INTERVAL = 1000L;
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.MILLISECONDS;
    public static final Signal DEFAULT_SIGNAL = Signal.FLUSH;
    public static final Long DEFAULT_DELAY_TERMINATE = 1L;


    private Long interval = DEFAULT_INTERVAL;
    private TimeUnit unit = DEFAULT_UNIT;
    private Signal signal = DEFAULT_SIGNAL;
    private Long delayTerminate = DEFAULT_DELAY_TERMINATE;

    public TimerEmitterProcessorOptions() {

    }

    public TimerEmitterProcessorOptions(TimerEmitterProcessorOptions other) {
        super(other);
        interval = other.interval;
        unit = other.unit;
        signal = other.signal;
        delayTerminate = other.delayTerminate;
    }

    public TimerEmitterProcessorOptions(ProcessorOptions other) {
        super(other);
    }

    public TimerEmitterProcessorOptions(JsonObject json) {
        super(json);
        TimerEmitterProcessorOptionsConverter.fromJson(json, this);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        TimerEmitterProcessorOptionsConverter.toJson(this, json);
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
    public TimerEmitterProcessorOptions setInterval(Long interval) {
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
    public TimerEmitterProcessorOptions setUnit(TimeUnit unit) {
        this.unit = unit;
        return this;
    }

    /**
     * The signal to emit every interval.
     *
     * @return the signal to emit.
     */
    public Signal getSignal() {
        return signal;
    }

    /**
     * The signal to emit every interval.
     * <p>
     * Can be one of <code>FLUSH</code> or <code>TERMINATE</code>
     *
     * @param signal the signal to emit
     * @return this
     */
    public TimerEmitterProcessorOptions setSignal(Signal signal) {
        this.signal = signal;
        return this;
    }

    /**
     * The delay before sending <code>TERMINATE</code> signal.
     *
     * @return the delay before sending <code>TERMINATE</code>
     */
    public Long getDelayTerminate() {
        return delayTerminate;
    }

    /**
     * The delay before sending <code>TERMINATE</code> signal.
     * <p>
     * A <code>FLUSH</code> signal is always emitted before sending
     * <code>TERMINATE</code> thus setting delay between both emissions
     * can help the pipeline finish his job properly, especially when accumulating data.
     *
     * @param delayTerminate the delay before sending <code>TERMINATE</code>
     * @return this
     */
    public TimerEmitterProcessorOptions setDelayTerminate(Long delayTerminate) {
        this.delayTerminate = delayTerminate;
        return this;
    }


    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public TimerEmitterProcessorOptions setName(String name) {
        return (TimerEmitterProcessorOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public TimerEmitterProcessorOptions setType(String type) {
        return (TimerEmitterProcessorOptions) super.setType(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimerEmitterProcessorOptions)) return false;
        if (!super.equals(o)) return false;
        TimerEmitterProcessorOptions that = (TimerEmitterProcessorOptions) o;
        return Objects.equals(interval, that.interval) &&
                unit == that.unit &&
                signal == that.signal;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), interval, unit, signal);
    }

    @Override
    public String toString() {
        return "TimerEmitterProcessorOptions{" +
                "interval=" + interval +
                ", unit=" + unit +
                ", signal=" + signal +
                "} " + super.toString();
    }
}
