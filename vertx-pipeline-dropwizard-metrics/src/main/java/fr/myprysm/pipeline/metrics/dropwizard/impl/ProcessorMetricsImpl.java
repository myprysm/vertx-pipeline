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
import fr.myprysm.pipeline.metrics.ProcessorMetrics;
import io.vertx.ext.dropwizard.ThroughputMeter;

class ProcessorMetricsImpl extends AbstractMetrics implements ProcessorMetrics {
    private final ThroughputMeter eventReceived;
    private final ThroughputMeter eventSent;
    private final ThroughputMeter eventError;

    ProcessorMetricsImpl(MetricRegistry registry, String baseName) {
        super(registry, baseName);

        eventReceived = throughputMeter("event", "received");
        eventSent = throughputMeter("event", "sent");
        eventError = throughputMeter("event", "error");
    }

    @Override
    public void eventReceived() {
        eventReceived.mark();
    }

    @Override
    public void eventSent() {
        eventSent.mark();
    }

    @Override
    public void eventError() {
        eventError.mark();
    }

    @Override
    public void close() {
    }
}
