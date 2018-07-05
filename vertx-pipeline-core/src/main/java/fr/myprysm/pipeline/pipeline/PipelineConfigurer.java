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

import fr.myprysm.pipeline.processor.Processor;
import fr.myprysm.pipeline.processor.ProcessorOptions;
import fr.myprysm.pipeline.pump.Pump;
import fr.myprysm.pipeline.pump.PumpOptions;
import fr.myprysm.pipeline.sink.Sink;
import fr.myprysm.pipeline.sink.SinkOptions;
import fr.myprysm.pipeline.util.ClasspathHelpers;
import fr.myprysm.pipeline.util.Holder;
import fr.myprysm.pipeline.util.Options;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static strman.Strman.*;

class PipelineConfigurer extends PipelineOptions implements Options {
    private static final Logger LOG = LoggerFactory.getLogger(PipelineConfigurer.class);

    private String controlChannel = null;
    private Triple<String, String, DeploymentOptions> sink;
    private LinkedList<List<Triple<String, String, DeploymentOptions>>> processors;
    private Triple<String, String, DeploymentOptions> pump;


    public PipelineConfigurer(JsonObject config) {
        super(config);
        if (config.getValue("controlChannel") instanceof String) {
            this.controlChannel = (String) config.getValue("controlChannel");
        }
    }


    /**
     * Returns the configuration of the {@link Sink} as {@link Triple} of:
     * <ul>
     * <li>Sink name</li>
     * <li>Sink class</li>
     * <li>Sink deployment options</li>
     * </ul>
     *
     * @return the configuration to deploy the sink
     */
    Triple<String, String, DeploymentOptions> getSinkDeployment() {
        checkBuild();
        return sink;
    }

    /**
     * Returns the global configuration to deploy
     * the whole {@link Processor} chain of the {@link PipelineVerticle}.
     * <p>
     * A list of deployments may contain only one element.
     * The global list of deployments may be empty if no processor has been configured.
     *
     * @return the list of multi-instance processor configurations
     */
    LinkedList<List<Triple<String, String, DeploymentOptions>>> getProcessorDeployments() {
        checkBuild();
        return processors;
    }

    /**
     * Returns the configuration of the {@link Pump} as {@link Triple} of:
     * <ul>
     * <li>Pump name</li>
     * <li>Pump class</li>
     * <li>Pump deployment options</li>
     * </ul>
     *
     * @return the configuration to deploy the sink
     */
    Triple<String, String, DeploymentOptions> getPumpDeployment() {
        checkBuild();
        return pump;
    }

    /**
     * Returns the control channel on which the pipeline communicates.
     *
     * @return the control channel
     */
    String getControlChannel() {
        checkBuild();
        return controlChannel;
    }

    private void checkBuild() {
        if (sink == null || processors == null || pump == null || controlChannel == null) {
            build();
        }
    }

    private void build() {
        prepareControlChannel();
        prepareSink();
        prepareProcessors();
        preparePump();
        buildNetwork();
    }

    private void prepareControlChannel() {
        if (controlChannel == null) {
            controlChannel = UUID.randomUUID().toString();
        }
    }


    /**
     * Travels from {@link Sink} to {@link Pump} through {@link Processor}s
     * to configure address lists.
     * <p>
     * It will reuse all the generated listening addresses to bind them
     * on the publisher side.
     */
    private void buildNetwork() {
        if (sink != null && pump != null) {
            final Holder<JsonArray> to = new Holder<>(arr());
            to.get().add(sink.getRight().getConfig().getString("from"));

            processors.descendingIterator().forEachRemaining(processorSet -> {
                JsonArray newTo = new JsonArray();
                for (Triple<String, String, DeploymentOptions> processor : processorSet) {

                    // Shuffle addresses so that processors will not send message sequentially
                    // to the next level in the chain.
                    processor.getRight().getConfig().put("to", shuffle(to.get().copy()));
                    String from = processor.getRight().getConfig().getString("from");
                    newTo.add(from);
                }

                to.set(newTo);
            });

            pump.getRight().getConfig().put("to", to.get());

            // Reverse processors to start from the closest to sink.
            Collections.reverse(processors);
        }
    }

    private JsonArray shuffle(JsonArray array) {
        List list = array.getList();
        Collections.shuffle(list);
        return new JsonArray(list);
    }

    private void prepareSink() {
        if (getSink() != null && !getSink().isEmpty()) {
            JsonObject config = getSink();
            config.put("type", resolveSink(config.getString("type")));
            SinkOptions options = new SinkOptions(config);
            // Get the position of the sink in the pipeline to allow easier ordering for metrics
            int position = getProcessors().size() + 1;
            String name = prepareName(getName(), options.getName(), SinkOptions.DEFAULT_NAME, options.getType(), "sink", "-" + position + "-1");
            sink = Triple.of(name, options.getType(), getDeploymentOptions(config, name, true));
        }
    }

    /**
     * Resolves the final class name for a sink.
     * <p>
     * Resolves the name first with the alias.
     * If no match found, then type is the final type.
     *
     * @param type the sink to resolve
     * @return the fully qualified class name to the sink
     */
    private String resolveSink(String type) {
        String clazz = ClasspathHelpers.getSinkForAlias(type);
        return clazz != null ? clazz : type;
    }

    /**
     * Prepares the processors.
     * It assigns their position in the chain for naming.
     */
    private void prepareProcessors() {
        JsonArray options = getProcessors();
        if (options == null || options.isEmpty()) {
            processors = new LinkedList<>();
        } else {
            processors = IntStream.range(0, options.size())
                    .mapToObj(i -> Pair.of(i + 1, options.getJsonObject(i)))
                    .map(this::prepareProcessorSet)
                    .collect(toCollection(LinkedList::new));
        }
    }

    /**
     * Prepares a processor set based on the number of instances requested.
     * Processor <code>from</code> address is generated here.
     *
     * @param configPair the config as a pair of position in pipeline and configuration
     * @return the prepared processor set deployment
     */
    private List<Triple<String, String, DeploymentOptions>> prepareProcessorSet(Pair<Integer, JsonObject> configPair) {
        JsonObject config = configPair.getRight();
        config.put("type", resolveProcessor(config.getString("type")));
        ProcessorOptions options = new ProcessorOptions(config);

        // Forces only one instance for accumulators
        if (ClasspathHelpers.getAccumulatorClassNames().contains(config.getString("type"))) {
            options.setInstances(1);
            config.put("loadFactor", 1);
        }

        return IntStream.rangeClosed(1, options.getInstances())
                .mapToObj(i -> prepareProcessor(config, options, configPair.getLeft(), i))
                .collect(toList());
    }

    /**
     * Resolves the final class name for a processor.
     * <p>
     * Resolves the name first with the alias.
     * If no match found, then type is the final type.
     *
     * @param type the processor to resolve
     * @return the fully qualified class name to the processor
     */
    private String resolveProcessor(String type) {
        String clazz = ClasspathHelpers.getProcessorForAlias(type);
        return clazz != null ? clazz : type;
    }

    /**
     * Prepares a single processor with the options provided.
     * <p>
     * It prepares its name and its <code>from</code> address.
     *
     * @param config   the raw configuration
     * @param options  the processor options
     * @param position the position in the processor chain
     * @param instance the instance
     * @return the deployment options
     */
    private Triple<String, String, DeploymentOptions> prepareProcessor(JsonObject config, ProcessorOptions options, Integer position, Integer instance) {
        String name = prepareName(
                getName(),
                options.getName(),
                ProcessorOptions.DEFAULT_NAME,
                options.getType(),
                "processor",
                "-" + Integer.toString(position) + "-" + Integer.toString(instance)); // Append position of the set in the pipeline + instance nb

        return Triple.of(name, options.getType(), getDeploymentOptions(config, name));
    }

    /**
     * Prepares the pump.
     * <p>
     * It prepares its name and its <code>from</code> address.
     */
    private void preparePump() {
        if (getPump() != null && !getPump().isEmpty()) {
            JsonObject config = getPump();
            config.put("type", resolvePump(config.getString("type")));
            PumpOptions options = new PumpOptions(config);
            String name = prepareName(getName(), options.getName(), PumpOptions.DEFAULT_NAME, options.getType(), "pump", "-0-1");
            pump = Triple.of(name, options.getType(), getDeploymentOptions(config, name, false));
        }
    }

    /**
     * Resolves the final class name for a pump.
     * <p>
     * Resolves the name first with the alias.
     * If no match found, then type is the final type.
     *
     * @param type the pump to resolve
     * @return the fully qualified class name to the pump
     */
    private String resolvePump(String type) {
        String clazz = ClasspathHelpers.getPumpForAlias(type);
        return clazz != null ? clazz : type;
    }

    /**
     * Prepares a verticle name.
     * <p>
     * Uses the <code>type</code> in place of name if this is the default.
     * Uses the <code>name</code> otherwise.
     * When name is not the default, the component type is automatically added.
     * <p>
     * Does not add a final dash if suffix is not provided (<code>null</code> or empty string)
     * <p>
     * The name is kebab-cased.
     *
     * @param prefix      the prefix
     * @param name        the name
     * @param defaultName the default name
     * @param type        the class name
     * @param component   one of "sink", "pump", "processor"
     * @param position    the position of the the component in the chain, must be something like "-POSITION-INSTANCE".
     * @return a formatted and standardised name for the verticle
     */
    private String prepareName(String prefix, String name, String defaultName, String type, String component, String position) {
        String finalName = toKebabCase(prefix);

        if (isNotBlank(position)) {
            finalName += position;
        }

        if (finalName.charAt(finalName.length() - 1) != '-') {
            finalName += "-";
        }


        if (!name.equals(defaultName)) {
            finalName += concatNames(name, component);
        } else {
            finalName += concatNames(type.substring(lastIndexOf(type, ".") + 1));
        }

        return finalName;
    }

    /**
     * Builds deployment options for the verticle.
     * <p>
     * If <code>loadFactor</code> is defined, then sets the number of verticle instances that will be deployed.
     * <p>
     * If <code>ha</code> is set to <code>true</code> then the verticle is deployed in HA mode.
     *
     * @param config the config to prepare deployment options
     * @param name   the name of the component
     * @return the options for deployment
     */
    private DeploymentOptions getDeploymentOptions(JsonObject config, String name) {
        return getDeploymentOptions(config, name, true);
    }

    /**
     * Builds deployment options for the verticle.
     * <p>
     * If <code>loadFactor</code> is defined, then sets the number of verticle instances that will be deployed.
     * <p>
     * If <code>ha</code> is set to <code>true</code> then the verticle is deployed in HA mode.
     * <p>
     * If <code>setAddress</code> is <code>true</code> then generates its listening address.
     *
     * @param config     the config to prepare deployment options
     * @param name       the name of the component
     * @param setAddress indicates whether the listening address must be generated
     * @return the options for deployment
     */
    private DeploymentOptions getDeploymentOptions(JsonObject config, String name, Boolean setAddress) {
        JsonObject copy = config.copy();
        DeploymentOptions options = new DeploymentOptions().setConfig(copy);

        copy.put("name", name);
        copy.put("controlChannel", controlChannel);
        if (setAddress) {
            String uuid = UUID.randomUUID().toString();
            copy.put("from", uuid);
            LOG.debug("[{}] bound to {}", name, uuid);
        }
        options.setInstances(copy.getInteger("loadFactor", 1));
        options.setHa(copy.getBoolean("ha", false));

        LOG.debug("[{}] configured: {}", name, options.toJson());
        return options;
    }

    private String concatNames(String... parts) {
        return toKebabCase(join(parts, "-"));
    }

}