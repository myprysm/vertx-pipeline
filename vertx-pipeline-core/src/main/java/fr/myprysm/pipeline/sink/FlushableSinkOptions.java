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

package fr.myprysm.pipeline.sink;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject(generateConverter = true)
public class FlushableSinkOptions extends SinkOptions {
    public static final Integer DEFAULT_BATCH_SIZE = 10;

    private Integer batchSize = DEFAULT_BATCH_SIZE;

    public FlushableSinkOptions() {
        super();
    }

    public FlushableSinkOptions(JsonObject json) {
        super(json);
        FlushableSinkOptionsConverter.fromJson(json, this);
    }

    public FlushableSinkOptions(FlushableSinkOptions other) {
        super(other);
        batchSize = other.batchSize;
    }

    public FlushableSinkOptions(SinkOptions other) {
        super(other);
    }

    /**
     * The batch size of the {@link FileSink}.
     *
     * @return the batch size
     */
    public Integer getBatchSize() {
        return batchSize;
    }

    /**
     * The batch size of the {@link FileSink}.
     * <p>
     * It must be a positive {@link Integer}.
     * <p>
     * It defaults to <code>10</code>
     *
     * @param batchSize the batch size to flush items.
     * @return this
     */
    public FlushableSinkOptions setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        FlushableSinkOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlushableSinkOptions)) return false;
        if (!super.equals(o)) return false;
        FlushableSinkOptions that = (FlushableSinkOptions) o;
        return Objects.equals(batchSize, that.batchSize);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), batchSize);
    }

    @Override
    public String toString() {
        return "FlushableSinkOptions{" +
                "batchSize=" + batchSize +
                "} " + super.toString();
    }
}
