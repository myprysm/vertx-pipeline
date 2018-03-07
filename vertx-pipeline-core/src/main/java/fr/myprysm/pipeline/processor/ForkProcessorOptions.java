/*
 * Copyright 2018 the original author or the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.myprysm.pipeline.processor;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@DataObject(generateConverter = true)
public class ForkProcessorOptions extends ProcessorOptions {

    private List<String> publish = new ArrayList<>();
    private List<String> send = new ArrayList<>();

    public ForkProcessorOptions() {

    }

    public ForkProcessorOptions(ForkProcessorOptions other) {
        super(other);
        publish = other.publish;
        send = other.send;
    }

    public ForkProcessorOptions(ProcessorOptions other) {
        super(other);
    }

    public ForkProcessorOptions(JsonObject json) {
        super(json);
        ForkProcessorOptionsConverter.fromJson(json, this);
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
    public ForkProcessorOptions setPublish(List<String> publish) {
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
    public ForkProcessorOptions setSend(List<String> send) {
        this.send = send;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public ForkProcessorOptions setName(String name) {
        return (ForkProcessorOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public ForkProcessorOptions setType(String type) {
        return (ForkProcessorOptions) super.setType(type);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        ForkProcessorOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForkProcessorOptions)) return false;
        if (!super.equals(o)) return false;
        ForkProcessorOptions that = (ForkProcessorOptions) o;
        return Objects.equals(publish, that.publish) &&
                Objects.equals(send, that.send);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), publish, send);
    }

    @Override
    public String toString() {
        return "ForkProcessor{" +
                "publish=" + publish +
                ", send=" + send +
                '}';
    }
}
