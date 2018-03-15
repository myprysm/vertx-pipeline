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

import fr.myprysm.pipeline.processor.Processor;
import fr.myprysm.pipeline.pump.Pump;
import fr.myprysm.pipeline.sink.Sink;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.metrics.Metrics;

/**
 * Provides metrics object to each component of a pipeline.
 */
public interface MetricsService extends Metrics {


    /**
     * Provides a metrics object for a processor
     *
     * @param processor the processor
     * @return metrics
     */
    ProcessorMetrics forProcessor(Processor processor);

    /**
     * Provides a metrics object for a sink
     *
     * @param sink the sink
     * @return metrics
     */
    SinkMetrics forSink(Sink sink);

    /**
     * Provides a metrics object for a pump
     *
     * @param pump the pump
     * @return metrics
     */
    PumpMetrics forPump(Pump pump);

    /**
     * Returns the metrics for <code>baseName</code> as a {@link JsonObject}.
     *
     * @param baseName base name to search
     * @return the metrics as a json object
     */
    JsonObject metrics(String baseName);

    /**
     * Returns all the metrics as a json object.
     *
     * @return the metrics.
     */
    JsonObject metrics();
}
