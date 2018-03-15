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

import fr.myprysm.pipeline.VertxTest;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.LinkedList;
import java.util.List;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PipelineConfigurerTest implements VertxTest {

    private Triple<String, String, DeploymentOptions> sink;
    private Triple<String, String, DeploymentOptions> pump;
    private LinkedList<List<Triple<String, String, DeploymentOptions>>> processors;
    private JsonObject config;

    @BeforeAll
    void setUp(Vertx vertx, VertxTestContext ctx) {
        ConfigStoreOptions store = new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject().put("path", "test-config.yml"));
        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(store));
        retriever.getConfig(ctx.succeeding(json -> {
            config = json;
            ctx.completeNow();
        }));
    }

    @Test
    @DisplayName("Pipeline configuration is empty")
    void testEmptyPipelineConfiguration() {
        setPipeline("empty");
        assertThat(sink).isNull();
        assertThat(pump).isNull();
        assertThat(processors).isEmpty();
    }

    @Test
    @DisplayName("Simple pipeline with no processor")
    void testSimplePipeline() {
        setPipeline("simple");
        assertThat(sink.getLeft()).isEqualTo("simple-1-1-console-sink");
        assertThat(sink.getMiddle()).isEqualTo("fr.myprysm.pipeline.sink.ConsoleSink");
        assertThat(pump.getLeft()).isEqualTo("simple-0-1-timer-pump");
        assertThat(pump.getMiddle()).isEqualTo("fr.myprysm.pipeline.pump.TimerPump");
        assertThat(processors).isEmpty();

        // check that pump is bound to sink and can only address the sink.
        JsonArray to = pump.getRight().getConfig().getJsonArray("to");
        assertThat(to).contains(sink.getRight().getConfig().getString("from"));
    }

    @Test
    @DisplayName("Basic pipeline with one processor")
    void testBasicPipeline() {
        setPipeline("basic");
        assertThat(sink.getLeft()).isEqualTo("basic-2-1-console-sink");
        assertThat(sink.getMiddle()).isEqualTo("fr.myprysm.pipeline.sink.ConsoleSink");
        assertThat(pump.getLeft()).isEqualTo("basic-0-1-timer-pump");
        assertThat(pump.getMiddle()).isEqualTo("fr.myprysm.pipeline.pump.TimerPump");
        assertThat(processors.size()).isEqualTo(1);

        List<Triple<String, String, DeploymentOptions>> processorSet = processors.get(0);

        assertThat(processorSet.size()).isEqualTo(1);
        Triple<String, String, DeploymentOptions> processor = processorSet.get(0);
        assertThat(processor.getLeft()).isEqualTo("basic-1-1-no-op-processor");
        assertThat(processor.getMiddle()).isEqualTo("fr.myprysm.pipeline.processor.NoOpProcessor");
    }

    @Test
    @DisplayName("Pipeline with custom names")
    void testPipelineNames() {
        setPipeline("named");
        assertThat(sink.getLeft()).isEqualTo("named-2-1-foo-bar-sink");
        assertThat(sink.getMiddle()).isEqualTo("fr.myprysm.pipeline.sink.ConsoleSink");
        assertThat(pump.getLeft()).isEqualTo("named-0-1-custom-name-pump");
        assertThat(pump.getMiddle()).isEqualTo("fr.myprysm.pipeline.pump.TimerPump");
        assertThat(processors.size()).isEqualTo(1);

        JsonArray to = pump.getRight().getConfig().getJsonArray("to");
        assertProcessor(processors.get(0).get(0), to,
                "fr.myprysm.pipeline.processor.NoOpProcessor",
                "named",
                1,
                1, "bla-bla-processor");
    }

    @Test
    @DisplayName("Pipeline with a multi instance processor")
    void testMultiInstanceProcessor() {
        setPipeline("multi-instance-processor");
        assertThat(sink.getLeft()).isEqualTo("multi-instance-processor-2-1-console-sink");
        assertThat(sink.getMiddle()).isEqualTo("fr.myprysm.pipeline.sink.ConsoleSink");
        assertThat(pump.getLeft()).isEqualTo("multi-instance-processor-0-1-timer-pump");
        assertThat(pump.getMiddle()).isEqualTo("fr.myprysm.pipeline.pump.TimerPump");
        assertThat(processors.size()).isEqualTo(1);

        List<Triple<String, String, DeploymentOptions>> processorSet = processors.get(0);

        assertThat(processorSet.size()).isEqualTo(10);
        for (int i = 0, max = processorSet.size(); i < max; i++) {
            Triple<String, String, DeploymentOptions> processor = processorSet.get(i);
            assertThat(processor.getLeft()).isEqualTo("multi-instance-processor" + "-1-" + (i + 1) + "-no-op-processor");
            assertThat(processor.getMiddle()).isEqualTo("fr.myprysm.pipeline.processor.NoOpProcessor");
        }
    }

    @Test
    @DisplayName("Pipeline with two multi instance processor")
    void testMultiInstanceMultiProcessor() {
        setPipeline("multi-instance-multi-processor");
        assertThat(sink.getLeft()).isEqualTo("multi-instance-multi-processor-3-1-file-sink");
        assertThat(sink.getMiddle()).isEqualTo("fr.myprysm.pipeline.sink.FileSink");
        assertThat(pump.getLeft()).isEqualTo("multi-instance-multi-processor-0-1-timer-pump");
        assertThat(pump.getMiddle()).isEqualTo("fr.myprysm.pipeline.pump.TimerPump");
        assertThat(processors.size()).isEqualTo(2);

        JsonArray to = pump.getRight().getConfig().getJsonArray("to");
        List<Triple<String, String, DeploymentOptions>> processorSet = processors.get(1);

        assertThat(processorSet.size()).isEqualTo(10);

        for (int i = 0, max = processorSet.size(); i < max; i++) {
            assertProcessor(processorSet.get(i),
                    to,
                    "fr.myprysm.pipeline.processor.NoOpProcessor",
                    "multi-instance-multi-processor",
                    1,
                    i + 1,
                    "no-op-processor"
            );
        }

        to = processorSet.get(0).getRight().getConfig().getJsonArray("to");
        processorSet = processors.get(0);

        assertThat(processorSet.size()).isEqualTo(3);

        for (int i = 0, max = processorSet.size(); i < max; i++) {
            assertProcessor(processorSet.get(i),
                    to,
                    "fr.myprysm.pipeline.processor.LogProcessor",
                    "multi-instance-multi-processor",
                    2,
                    i + 1,
                    "log-processor"
            );
        }

        to = processorSet.get(0).getRight().getConfig().getJsonArray("to");
        assertThat(to).contains(sink.getRight().getConfig().getString("from"));
    }

    @Test
    @DisplayName("Accumulator in a pipeline should be single instance")
    void testAccumulatorIsSingleInstance() {
        setPipeline("accumulator-single-instance-test");
        assertThat(sink.getLeft()).isEqualTo("accumulator-single-instance-test-4-1-blackhole-sink");
        assertThat(sink.getMiddle()).isEqualTo("fr.myprysm.pipeline.sink.BlackholeSink");
        assertThat(pump.getLeft()).isEqualTo("accumulator-single-instance-test-0-1-timer-pump");
        assertThat(pump.getMiddle()).isEqualTo("fr.myprysm.pipeline.pump.TimerPump");
        assertThat(processors.size()).isEqualTo(3);

        List<Triple<String, String, DeploymentOptions>> processorSet = processors.get(0);

        assertThat(processorSet.size()).isEqualTo(1);
        Triple<String, String, DeploymentOptions> processor = processorSet.get(0);
        assertThat(processor.getLeft()).isEqualTo("accumulator-single-instance-test-3-1-merge-basic-processor");
        assertThat(processor.getMiddle()).isEqualTo("fr.myprysm.pipeline.processor.MergeBasicProcessor");
        assertThat(processor.getRight().getInstances()).isEqualTo(1);
    }

    /**
     * Assert processor configuration ({@link Triple}) against the provided values.
     *
     * @param processor     the {@link Triple} representing the processor configuration
     * @param to            the recipient list
     * @param clazz         the type of the processor
     * @param baseName      basename of the processor
     * @param group         the group of the processor (basically the position in the chain)
     * @param instance      the instance of the processor (basically its index when <code>instances</code> is greater than <code>1</code>)
     * @param processorName name of the processor
     */
    private void assertProcessor(Triple<String, String, DeploymentOptions> processor, JsonArray to, String clazz, String baseName, int group, int instance, String processorName) {
        assertThat(processor.getLeft()).isEqualTo(baseName + "-" + group + "-" + instance + "-" + processorName);
        assertThat(processor.getMiddle()).isEqualTo(clazz);
        assertThat(to).contains(processor.getRight().getConfig().getString("from"));
    }

    /**
     * Build a new {@link PipelineConfigurer} from the input {@link JsonObject}
     *
     * @param config the json config
     * @return the configuration object
     */
    private PipelineConfigurer fromConfig(JsonObject config) {
        return new PipelineConfigurer(config);
    }

    /**
     * sets the configuration for test a named pipeline
     * when no configuration is found, an empty {@link JsonObject} is created.
     *
     * @param pipeline the name of the pipeline
     */
    void setPipeline(String pipeline) {
        JsonObject conf = config.getJsonObject(pipeline);
        if (conf == null) {
            conf = obj();
        }
        conf.put("name", pipeline);

        PipelineConfigurer configurer = fromConfig(conf);
        sink = configurer.getSinkDeployment();
        pump = configurer.getPumpDeployment();
        processors = configurer.getProcessorDeployments();
    }

}