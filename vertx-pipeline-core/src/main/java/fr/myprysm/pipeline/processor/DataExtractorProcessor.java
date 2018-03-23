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

import fr.myprysm.pipeline.util.Alias;
import fr.myprysm.pipeline.util.JsonHelpers;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.netty.channel.EventLoop;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Future;

import java.util.Map;
import java.util.Optional;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static io.reactivex.Completable.complete;

/**
 * This processor provides capability to extract any input json path
 * to any output json path.
 * <p>
 * In case the input path does not exists or is <code>null</code> the processor will ensure that
 * a <code>null</code> value will be set at the output path to ensure that the
 * chain will not be broken by checking unexisting path where error can be handled
 * in a more elegant manner and produce more relevant output.
 * <p>
 * This processor runs its transformations on a worker thread to avoid blocking the {@link EventLoop}.
 */
@Alias(prefix = "pipeline-core", name = "data-extractor-processor")
public class DataExtractorProcessor extends BaseJsonProcessor<DataExtractorProcessorOptions> {
    public static final String THIS = "$event";

    /**
     * Stored as a {@link JsonObject}
     * but this is basically a <code>Map&lt;String, String&gt;</code> that holds
     * as key the path to extract from input objects and as value the
     */
    private JsonObject extract;

    @Override
    public Single<JsonObject> transform(JsonObject input) {
        return vertx.rxExecuteBlocking(complete -> this.extractData(input, complete));
    }

    /**
     * Translates the values from an input field to an output field.
     * <p>
     * Retrieves <code>null</code> values when the input path does not exist,
     * creates the output path in any case.
     * <p>
     * Asynchronous operation executed on a worker thread.
     *
     * @param complete the {@link Future} to complete once data is fully transformed.
     */
    private void extractData(JsonObject input, Future<JsonObject> complete) {
        JsonObject output = obj();
        for (Map.Entry<String, Object> mapping : extract) {
            Optional<Object> value;

            if (THIS.equals(mapping.getKey())) {
                value = Optional.of(input);
            } else {
                value = JsonHelpers.extractObject(input, mapping.getKey());
            }

            if (value.isPresent()) {
                if (THIS.equals(mapping.getKey()) && value.get() instanceof JsonObject) {
                    JsonHelpers.writeObject(output, (String) mapping.getValue(), ((JsonObject) value.get()).copy());
                } else {
                    JsonHelpers.writeObject(output, (String) mapping.getValue(), value.get());
                }
            } else {
                JsonHelpers.ensurePathExistsAndGet(output, (String) mapping.getValue());
            }
        }
        complete.complete(output);
    }

    @Override
    public DataExtractorProcessorOptions readConfiguration(JsonObject config) {
        return new DataExtractorProcessorOptions(config);
    }

    @Override
    protected Completable startVerticle() {
        return complete();
    }

    @Override
    public Completable configure(DataExtractorProcessorOptions config) {
        extract = config.getExtract();
        return complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return DataExtractorProcessorOptionsValidation.validate(config);
    }
}
