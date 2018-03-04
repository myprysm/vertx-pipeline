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

package fr.myprysm.pipeline.pipeline;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;

@DataObject(generateConverter = true)
public class ExchangeOptions {
    public static final List<String> DEFAULT_TO = Collections.singletonList("to");
    public static final String DEFAULT_FROM = "from";
    public static final String DEFAULT_CONTROL_CHANNEL = "control";

    private String from;
    private List<String> to;
    private String controlChannel;

    public ExchangeOptions() {

    }

    public ExchangeOptions(ExchangeOptions other) {
        from = other.from;
        to = other.to;
        controlChannel = other.controlChannel;
    }

    public ExchangeOptions(JsonObject json) {
        ExchangeOptionsConverter.fromJson(json, this);
    }

    /**
     * The address the deployed object will send its result to.
     *
     * @return the address the deployed object will send results to.
     */
    public List<String> getTo() {
        return to;
    }

    /**
     * The addresses the deployed object will send results to.
     * <p>
     * This is automatically configured when the pipeline is built.
     * <b>The address cannot be configured</b>
     *
     * @param to the addresses the deployed object will send results to..
     * @return this
     */
    public ExchangeOptions setTo(List<String> to) {
        this.to = unmodifiableList(to);
        return this;
    }

    /**
     * The address the deployed object will receive items from.
     *
     * @return the address the deployed object will receive items from.
     */
    public String getFrom() {
        return from;
    }

    /**
     * The address the deployed object will receive items from.
     * <p>
     * This is automatically configured when the pipeline is built.
     * <b>The address cannot be configured</b>
     *
     * @param from the address the deployed object will receive items from.
     * @return this
     */
    public ExchangeOptions setFrom(String from) {
        this.from = from;
        return this;
    }

    /**
     * The control channel to emit/receive signals.
     * <p>
     * This is automatically configured when the pipeline is built.
     * <b>The channel cannot be configured</b>
     *
     * @return the control channel
     */
    public String getControlChannel() {
        return controlChannel;
    }

    /**
     * The control channel to emit/receive signals.
     * <p>
     * This is automatically configured when the pipeline is built.
     * <b>The channel cannot be configured</b>
     *
     * @param controlChannel the control channel
     * @return this
     */
    public ExchangeOptions setControlChannel(String controlChannel) {
        this.controlChannel = controlChannel;
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        ExchangeOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public String toString() {
        return "ExchangeOptions{" +
                "from='" + from + '\'' +
                ", to=" + to +
                ", controlChannel='" + controlChannel + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeOptions)) return false;
        ExchangeOptions that = (ExchangeOptions) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) &&
                Objects.equals(controlChannel, that.controlChannel);
    }

    @Override
    public int hashCode() {

        return Objects.hash(from, to, controlChannel);
    }
}
