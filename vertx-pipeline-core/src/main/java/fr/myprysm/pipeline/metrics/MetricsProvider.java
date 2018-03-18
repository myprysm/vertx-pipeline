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

package fr.myprysm.pipeline.metrics;

import fr.myprysm.pipeline.metrics.impl.DummyMetricsServiceImpl;
import fr.myprysm.pipeline.processor.Processor;
import fr.myprysm.pipeline.pump.Pump;
import fr.myprysm.pipeline.sink.Sink;
import io.vertx.core.json.JsonObject;

/**
 * Provides Metrics to components and results as JSON.
 */
public class MetricsProvider {

    private static MetricsService metrics = new DummyMetricsServiceImpl();

    /**
     * Provdes metrics for a {@link Sink}
     *
     * @param sink the sink
     * @return the metrics
     */
    public static SinkMetrics forSink(Sink sink) {
        return metrics.forSink(sink);
    }

    /**
     * Provides metrics for a {@link Pump}
     *
     * @param pump pump
     * @return the metrics
     */
    public static PumpMetrics forPump(Pump pump) {
        return metrics.forPump(pump);
    }

    /**
     * Provides metrics for a {@link Processor}
     *
     * @param processor the processor
     * @return the metrics
     */
    public static ProcessorMetrics forProcessor(Processor processor) {
        return metrics.forProcessor(processor);
    }

    /**
     * Get the current metrics service
     *
     * @return the metrics service
     */
    public static MetricsService getMetricsService() {
        return metrics;
    }

    /**
     * Initializes the provider with a metrics service.
     *
     * @param metricsService the metrics service
     */
    public static void initialize(MetricsService metricsService) {
        metrics = metricsService;
    }

    /**
     * Closes the metrics service
     */
    public static void close() {
        metrics.close();
    }

    /**
     * Provides the current metrics for a component name
     *
     * @param name the component name
     * @return the metrics
     */
    public static JsonObject getMetrics(String name) {
        return metrics.metrics(name);
    }

    /**
     * Provides the current global metrics.
     *
     * @return the metrics
     */
    public static JsonObject getMetrics() {
        return metrics.metrics();
    }
}
