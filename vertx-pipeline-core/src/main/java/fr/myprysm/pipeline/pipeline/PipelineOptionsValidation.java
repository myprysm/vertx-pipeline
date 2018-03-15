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

package fr.myprysm.pipeline.pipeline;

import fr.myprysm.pipeline.validation.JsonValidation;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Observable;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.tuple.Pair;

import static fr.myprysm.pipeline.util.ClasspathHelpers.*;
import static fr.myprysm.pipeline.util.JsonHelpers.extractString;
import static fr.myprysm.pipeline.validation.JsonValidation.*;

public interface PipelineOptionsValidation {

    static ValidationResult validate(JsonObject config) {
        return nameIsOnlyCharactersAndPunctuation()
                .and(hasPump().and(pumpExists()))
                .and(hasSink().and(sinkExists()))
                .and(isNull("processors")
                        .or(isArray("processors").and(processorsExist()))
                )
                .apply(config);
    }

    static JsonValidation nameIsOnlyCharactersAndPunctuation() {
        return matches("name", "[a-zA-Z.-]+", "Name should contain only letters, dashes and dots.");
    }

    static JsonValidation pumpExists() {
        return hasPath("pump.type")
                .and(holds(json -> getPumpClassNames().contains(extractString(json, "pump.type").orElse("")),
                        "The class is not a kind of Pump"));
    }

    static JsonValidation sinkExists() {
        return hasPath("sink.type")
                .and(holds(json -> getSinkClassNames().contains(extractString(json, "sink.type").orElse("")),
                        "The class is not a kind of Sink"));
    }

    /**
     * Validate that each processor in the chain has a type as well as a valid number of instances
     *
     * @return validation result
     */
    static JsonValidation processorsExist() {
        return (json) -> Observable.fromIterable(json.getJsonArray("processors"))
                .map(JsonObject.class::cast)
                .map(opt -> Pair.of(opt,
                        isString("type").and(holds(o -> getProcessorClassNames().contains(o.getString("type")), "The class is not a kind of Processor")).apply(opt)))
                .map(p -> Pair.of(p.getLeft(), p.getRight().and(() -> isNull("instances").or(gt("instances", 0L)).apply(p.getLeft()))))
                .map(Pair::getRight)
                .reduce((v1, v2) -> v1.and(() -> v2))
                .blockingGet();
    }


    static JsonValidation hasPump() {
        return has("pump");
    }

    static JsonValidation hasSink() {
        return has("sink");
    }

    static JsonValidation has(String component) {
        return isObject(component, "A " + component + " is required in order to allow a pipeline to start");
    }

}
