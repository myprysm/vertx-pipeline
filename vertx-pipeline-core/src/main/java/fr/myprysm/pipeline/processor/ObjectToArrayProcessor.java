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

import fr.myprysm.pipeline.util.JsonHelpers;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.netty.channel.EventLoop;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Future;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

import static fr.myprysm.pipeline.util.JsonHelpers.EMPTY_STRING;
import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static io.reactivex.Completable.complete;

/**
 * This processor provides capability to transform an input field to an array.
 * <p>
 * If the field is already an array, then it does nothing.
 * <p>
 * In case the input path does not exists or is <code>null</code> the processor will ensure that
 * an array will be set at the output path to ensure that the
 * chain will not be broken by checking unexisting path where error can be handled
 * in a more elegant manner and produce more relevant output.
 * <p>
 * This processor runs its transformations on a worker thread to avoid blocking the {@link EventLoop}.
 */
public class ObjectToArrayProcessor extends BaseJsonProcessor<ObjectToArrayProcessorOptions> {

    private JsonArray fields;

    @Override
    public Single<JsonObject> transform(JsonObject input) {
        return vertx.rxExecuteBlocking(complete -> this.extractData(input, complete));
    }

    private void extractData(JsonObject input, Future<JsonObject> complete) {
        JsonObject output = input.copy();
        for (Object o : fields) {
            String field = (String) o;
            JsonArray array = arr();
            Optional<Object> optional = JsonHelpers.extractObject(input, field);

            if (optional.isPresent()) {
                Object value = optional.get();
                if (value instanceof JsonObject) {
                    array.add(((JsonObject) value).copy());
                } else {
                    array.add(value);
                }

            }

            Pair<String, String> pathAndField = JsonHelpers.toPathAndField(field);
            if (EMPTY_STRING.equals(pathAndField.getLeft())) {
                output.put(pathAndField.getRight(), array);
            } else {
                JsonHelpers.ensurePathExistsAndGet(output, pathAndField.getLeft())
                        .put(pathAndField.getRight(), array);
            }
        }

        complete.complete(output);
    }

    @Override
    protected Completable startVerticle() {
        return complete();
    }

    @Override
    public ObjectToArrayProcessorOptions readConfiguration(JsonObject config) {
        return new ObjectToArrayProcessorOptions(config);
    }

    @Override
    public Completable configure(ObjectToArrayProcessorOptions config) {
        fields = config.getFields();
        return complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return ObjectToArrayProcessorOptionsValidation.validate(config);
    }
}
