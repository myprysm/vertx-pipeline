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

import fr.myprysm.pipeline.VertxTest;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class MergeBasicProcessorTest implements VertxTest {
    public static final String VERTICLE = "fr.myprysm.pipeline.processor.DataExtractorProcessor";
    static final JsonObject CONFIG = obj()
            .put("name", "merge-basic-test")
            .put("type", VERTICLE)
            .put("from", "from")
            .put("to", arr().add("to"))
            .put("controlChannel", "channel")
            .put("operations", obj()
                    .put("keyToObj", "nested.field")
                    .put("mergeArrays", "some.array")
                    .put("sortArray", obj()
                            .put("path", "some")
                            .put("field", "array")
                            .put("order", "DESC")
                            .put("type", "long")
                    )
            )
            .put("onFlush", obj()
                    .put("sort", obj()
                            .put("path", "some.string")
                            .put("type", "string")
                    )
            );
    static DeploymentOptions OPTIONS = new DeploymentOptions().setConfig(CONFIG);

    static final JsonObject INPUT = obj()
            .put("nested", obj().put("field", "a secret"))
            .put("some", obj().put("array", "another secret"));

    @Test
    @Disabled("Not on time to send the development case otherwise.")
    @DisplayName("Testing transformations with data extractors")
    void testDataExtractorProcessor(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        Checkpoint cp = ctx.checkpoint(10);
        vertx.eventBus().<JsonObject>consumer("to", message -> {
            JsonObject json = message.body();
            assertThat(json.getJsonObject("merge")).isNotNull();
//            assertThat(JsonHelpers.extractObject(json, "a.very.deep.path.down.below")).hasValue("a secret");
//            assertThat(JsonHelpers.extractObject(json, "foo.bar")).hasValue("another secret");
//            assertThat(JsonHelpers.extractObject(json, "copy")).hasValue(INPUT);
//            assertThat(JsonHelpers.extractObject(json, "to.an.empty.container")).hasValue(obj());
            cp.flag();
        });

        vertx.deployVerticle(VERTICLE, OPTIONS, ctx.succeeding(id -> {
            for (int i = 0, max = 15; i < max; i++) {
                vertx.eventBus().send("from", INPUT);
            }
        }));

        ctx.awaitCompletion(2, TimeUnit.SECONDS);
    }
}