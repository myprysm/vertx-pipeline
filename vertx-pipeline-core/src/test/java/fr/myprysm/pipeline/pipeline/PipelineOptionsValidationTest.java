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

import fr.myprysm.pipeline.BaseJsonValidationTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;

class PipelineOptionsValidationTest implements BaseJsonValidationTest {


    @Test
    @DisplayName("Ensure Pipeline options validator works")
    void testOptionValidation() {
        JsonObject o = obj();
        JsonArray a = arr();
        isInvalid(o, PipelineOptionsValidation::validate, "Field 'name' is null");

        o.put("name", "Foo234");
        isInvalid(o, PipelineOptionsValidation::validate, "Name should contain only letters, dashes and dots.");

        o.put("name", "foo-bar");
        isInvalid(o, PipelineOptionsValidation::validate,
                "A pump is required in order to allow a pipeline to start");

        o.put("pump", obj().put("type", "toto"));
        isInvalid(o, PipelineOptionsValidation::validate, "The class is not a kind of Pump");

        o.getJsonObject("pump").put("type", "fr.myprysm.pipeline.pump.TimerPump");
        isInvalid(o, PipelineOptionsValidation::validate, "A sink is required in order to allow a pipeline to start");

        isInvalid(o.put("sink", obj()), PipelineOptionsValidation::validate, "Field 'sink.type' does not exist");

        o.getJsonObject("sink").put("type", "toto");
        isInvalid(o, PipelineOptionsValidation::validate, "The class is not a kind of Sink");

        o.getJsonObject("sink").put("type", "fr.myprysm.pipeline.sink.ConsoleSink");

        isValid(o, PipelineOptionsValidation::validate);

        // Processors validation
        o.put("processors", a);
        a.add(obj());
        isInvalid(o, PipelineOptionsValidation::validate, "Invalid options for Processor 'null': Field 'type' is null");

        a.getJsonObject(0).put("type", "toto");
        isInvalid(o, PipelineOptionsValidation::validate, "Invalid options for Processor 'toto': The class is not a kind of Processor");

        a.getJsonObject(0).put("type", "fr.myprysm.pipeline.processor.NoOpProcessor");
        isValid(o, PipelineOptionsValidation::validate);

        a.add(obj());
        isInvalid(o, PipelineOptionsValidation::validate, "Invalid options for Processor 'null': Field 'type' is null");

        a.getJsonObject(1).put("type", "fr.myprysm.pipeline.processor.NoOpProcessor").put("instances", "test");
        isInvalid(o, PipelineOptionsValidation::validate, "Invalid options for Processor 'fr.myprysm.pipeline.processor.NoOpProcessor': Field 'instances' is not a long");

        a.getJsonObject(1).put("instances", 0);
        isInvalid(o, PipelineOptionsValidation::validate, "Invalid options for Processor 'fr.myprysm.pipeline.processor.NoOpProcessor': Field 'instances' is not greater than 0");

        a.getJsonObject(1).put("instances", 1);
        isValid(o, PipelineOptionsValidation::validate);
    }
}