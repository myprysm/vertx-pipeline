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

@DataObject(generateConverter = true)
public class CounterEmitterProcessorOptions extends ProcessorOptions {

    public static final Long DEFAULT_INTERVAL = 1000L;
    public static final Signal DEFAULT_SIGNAL = Signal.FLUSH;
    public static final Long DEFAULT_DELAY_TERMINATE = 1L;


    private Long interval = DEFAULT_INTERVAL;
    private Signal signal = DEFAULT_SIGNAL;
    private Long delayTerminate = DEFAULT_DELAY_TERMINATE;

    public CounterEmitterProcessorOptions() {

    }

    public CounterEmitterProcessorOptions(CounterEmitterProcessorOptions other) {
        super(other);
        interval = other.interval;
        signal = other.signal;
        delayTerminate = other.delayTerminate;
    }

    public CounterEmitterProcessorOptions(ProcessorOptions other) {
        super(other);
    }

    public CounterEmitterProcessorOptions(JsonObject json) {
        super(json);
        CounterEmitterProcessorOptionsConverter.fromJson(json, this);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        CounterEmitterProcessorOptionsConverter.toJson(this, json);
        return json;
    }

    /**
     * The interval of the counter.
     *
     * @return the interval of the counter.
     */
    public Long getInterval() {
        return interval;
    }

    /**
     * The interval of the counter.
     * <p>
     * The value must be a positive integer.
     * <p>
     * The counter will emit a signal every <code>interval</code> event received.
     *
     * @param interval the interval to set.
     * @return this
     */
    public CounterEmitterProcessorOptions setInterval(Long interval) {
        this.interval = interval;
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
    public CounterEmitterProcessorOptions setSignal(Signal signal) {
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
    public CounterEmitterProcessorOptions setDelayTerminate(Long delayTerminate) {
        this.delayTerminate = delayTerminate;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public CounterEmitterProcessorOptions setName(String name) {
        return (CounterEmitterProcessorOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public CounterEmitterProcessorOptions setType(String type) {
        return (CounterEmitterProcessorOptions) super.setType(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CounterEmitterProcessorOptions)) return false;
        if (!super.equals(o)) return false;
        CounterEmitterProcessorOptions that = (CounterEmitterProcessorOptions) o;
        return Objects.equals(interval, that.interval) &&
                signal == that.signal;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), interval, signal);
    }

    @Override
    public String toString() {
        return "TimerEmitterProcessorOptions{" +
                "interval=" + interval +
                ", signal=" + signal +
                "} " + super.toString();
    }
}
