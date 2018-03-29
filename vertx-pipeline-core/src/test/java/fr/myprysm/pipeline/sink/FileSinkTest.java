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

package fr.myprysm.pipeline.sink;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import fr.myprysm.pipeline.VertxTest;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class FileSinkTest implements VertxTest {
    private static final String PATH_JSON = "/tmp/output.json";
    private static final String PATH_YAML = "/tmp/output.yaml";

    private static final JsonObject MESSAGE = obj().put("foo", "bar");
    private static final String VERTICLE = "fr.myprysm.pipeline.sink.FileSink";
    private static FileSink verticle;

    private final JsonObject config = obj()
            .put("name", "file-sink-test")
            .put("type", VERTICLE)
            .put("from", "from")
            .put("controlChannel", "channel")
            .put("batchSize", 5)
            .put("path", "/tmp")
            .put("file", "output")
            .put("mode", "overwrite")
            .put("type", "json");
    private DeploymentOptions options = new DeploymentOptions().setConfig(config);

    @BeforeEach
    void setUp(Vertx vertx, VertxTestContext ctx) {
        cleanUpFiles(vertx);
        ctx.completeNow();
    }

    @Test
    @DisplayName("File sink should write in file")
    void testFileSinkWritesInFile(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        Checkpoint cp = ctx.checkpoint();
        vertx.deployVerticle(VERTICLE, options, ctx.succeeding(id -> {
            vertx.eventBus().send("channel", "flush");
            vertx.eventBus().send("channel", "FLUSH");
            vertx.eventBus().send("channel", "RESUME");

            for (int i = 0; i < 10; i++) {
                vertx.eventBus().send("from", MESSAGE);
            }
            vertx.setTimer(100, timer -> {
                vertx.undeploy(id, ctx.succeeding(zoid ->
                        vertx.fileSystem().readFile("/tmp/output.json", ctx.succeeding(buffer -> {
                            ctx.verify(() -> {
                                Arrays.stream(buffer.toString().split("\n"))
                                        .forEach(line -> assertThat(line).isEqualTo(MESSAGE.toString()));
                            });

                            cp.flag();
                        }))
                ));
            });
        }));

    }

    @Test
    @DisplayName("File sink should append in file")
    void testFileSinkAppendsInFile(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        vertx.fileSystem().writeFileBlocking(PATH_JSON, Buffer.buffer("{\"bar\":\"foo\"}\n"));
        config.put("batchSize", 1).put("mode", "append");
        verticle = new FileSink();
        vertx.deployVerticle(verticle, options, ctx.succeeding(id -> {
            assertThat(verticle.batchSize()).isEqualTo(1);
            assertThat(verticle.controlChannel()).isEqualTo("channel");
            for (int i = 0; i < 2; i++) {
                vertx.eventBus().send("from", MESSAGE);
            }
            vertx.undeploy(id, ctx.succeeding(zoid -> {
                vertx.fileSystem().readFile("/tmp/output.json", ctx.succeeding(buffer -> {
                    ctx.verify(() -> {
                        String[] lines = buffer.toString().split("\n");
                        assertThat(lines[0]).isEqualTo("{\"bar\":\"foo\"}");
                        assertThat(lines[1]).isEqualTo(MESSAGE.toString());
                        assertThat(lines[2]).isEqualTo(MESSAGE.toString());
                        ctx.completeNow();
                    });
                }));
            }));
        }));


        ctx.awaitCompletion(2, TimeUnit.SECONDS);
    }


    @Test
    @DisplayName("File sink should fail when file already exists")
    void testFileSinkShouldFailWhenFileExists(Vertx vertx, VertxTestContext ctx) throws InterruptedException {
        vertx.fileSystem().createFileBlocking(PATH_JSON, "rwxr-x---");
        config.put("mode", "fail");
        vertx.deployVerticle(VERTICLE, options, ctx.failing(zoid -> ctx.completeNow()));
        ctx.awaitCompletion(1, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("File sink should write also YAML")
    void testFileSinkShouldWriteYaml(Vertx vertx, VertxTestContext ctx) {
        config.put("format", "yaml").put("batchSize", 1);
        vertx.deployVerticle(VERTICLE, options, ctx.succeeding(id -> {

            vertx.eventBus().send("from", MESSAGE);
            vertx.eventBus().send("from", obj().put("bar", "baz"));
            vertx.eventBus().send("from", obj().put("field", "value"));
            vertx.eventBus().send("channel", "FLUSH");

            vertx.setTimer(100, timer -> vertx.undeploy(id, ctx.succeeding(zoid -> {
                vertx.fileSystem().readFile("/tmp/output.yaml", ctx.succeeding(buffer -> ctx.verify(() -> {
                    try {
                        JsonNode data = new YAMLMapper().readTree(buffer.toString());

                        assertThat(data).isInstanceOf(ObjectNode.class);
                        ctx.completeNow();
                    } catch (IOException e) {
                        ctx.failNow(e);
                    }
                })));
            })));

        }));
    }

    @AfterEach
    void tearDown(Vertx vertx, VertxTestContext ctx) {
        cleanUpFiles(vertx);
        ctx.completeNow();
    }

    void cleanUpFiles(Vertx vertx) {
        if (vertx.fileSystem().existsBlocking(PATH_JSON)) {
            vertx.fileSystem().deleteBlocking(PATH_JSON);
        }
        if (vertx.fileSystem().existsBlocking(PATH_YAML)) {
            vertx.fileSystem().deleteBlocking(PATH_YAML);
        }
    }
}