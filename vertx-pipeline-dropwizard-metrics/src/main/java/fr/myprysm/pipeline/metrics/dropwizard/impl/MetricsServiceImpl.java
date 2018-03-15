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

package fr.myprysm.pipeline.metrics.dropwizard.impl;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import fr.myprysm.pipeline.DeploymentVerticleOptions;
import fr.myprysm.pipeline.metrics.MetricsService;
import fr.myprysm.pipeline.metrics.ProcessorMetrics;
import fr.myprysm.pipeline.metrics.PumpMetrics;
import fr.myprysm.pipeline.metrics.SinkMetrics;
import fr.myprysm.pipeline.processor.Processor;
import fr.myprysm.pipeline.pump.Pump;
import fr.myprysm.pipeline.sink.Sink;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.impl.Helper;
import io.vertx.ext.dropwizard.reporters.JmxReporter;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MetricsServiceImpl implements MetricsService {
    public static final String REGISTRY_NAME = "vertx-pipeline";
    public static final String DOMAIN_NAME = "vertx-pipeline";
    private static final MetricRegistry REGISTRY = SharedMetricRegistries.getOrCreate(REGISTRY_NAME);

    private Boolean enabled;
    private Boolean jmxEnabled;
    private JmxReporter reporter;


    public MetricsServiceImpl(DeploymentVerticleOptions opts) {
        enabled = opts.getMetrics();
        jmxEnabled = opts.getJmxEnabled();
        if (jmxEnabled) {
            reporter = JmxReporter.forRegistry(REGISTRY).inDomain(DOMAIN_NAME).build();
            reporter.start();
        }
    }

    @Override
    public ProcessorMetrics forProcessor(Processor processor) {
        return new ProcessorMetricsImpl(REGISTRY, processor.name());
    }

    @Override
    public SinkMetrics forSink(Sink sink) {
        return new SinkMetricsImpl(REGISTRY, sink.name());
    }

    @Override
    public PumpMetrics forPump(Pump pump) {
        return new PumpMetricsImpl(REGISTRY, pump.name());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Will return the metrics that correspond with a given base name.
     *
     * @return the map of metrics where the key is the name of the metric (excluding the base name) and the value is
     * the json data representing that metric
     */
    @Override
    public JsonObject metrics(String baseName) {
        Map<String, Object> map = REGISTRY.getMetrics().
                entrySet().
                stream().
                filter(e -> e.getKey().startsWith(baseName)).
                collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Helper.convertMetric(e.getValue(), TimeUnit.SECONDS, TimeUnit.MILLISECONDS)));
        return new JsonObject(map);
    }

    @Override
    public JsonObject metrics() {
        return metrics("");
    }

    @Override
    public void close() {
        if (reporter != null) {
            reporter.close();
        }
    }
}
