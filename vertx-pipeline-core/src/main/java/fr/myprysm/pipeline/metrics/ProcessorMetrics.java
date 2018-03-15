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

import io.vertx.core.spi.metrics.Metrics;

/**
 * Metrics for a processor.
 * Counts the number of events received and sent as well as errors during processing.
 */
public interface ProcessorMetrics extends Metrics {

    /**
     * Marks an event reception
     */
    void eventReceived();

    /**
     * Marks an event emission
     */
    void eventSent();

    /**
     * Marks an error
     */
    void eventError();
}
