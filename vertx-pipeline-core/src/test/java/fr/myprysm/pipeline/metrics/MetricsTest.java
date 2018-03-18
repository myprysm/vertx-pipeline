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
import fr.myprysm.pipeline.processor.NoOpProcessor;
import fr.myprysm.pipeline.pump.TimerPump;
import fr.myprysm.pipeline.sink.BlackholeSink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

class MetricsTest {

    @Test
    @DisplayName("Test metrics provider")
    void testMetricsProvider() {
        MetricsProvider.initialize(new DummyMetricsServiceImpl());
        assertThat(MetricsProvider.getMetrics()).isEqualTo(obj());
        assertThat(MetricsProvider.getMetrics("test")).isEqualTo(obj());
        assertThat(MetricsProvider.getMetricsService()).isInstanceOf(DummyMetricsServiceImpl.class);
        MetricsProvider.close();
    }

    @Test
    @DisplayName("Test metrics service")
    void testMetricsService() {
        MetricsService service = MetricsProvider.getMetricsService();

        assertThat(service).isInstanceOf(DummyMetricsServiceImpl.class);
        assertThat(service.metrics()).isEqualTo(obj());
        assertThat(service.metrics("test")).isEqualTo(obj());
        assertThat(service.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Test metrics components")
    void testMetricsComponents() {
        PumpMetrics pumpMetrics = MetricsProvider.forPump(new TimerPump());
        assertThat(pumpMetrics.isEnabled()).isFalse();
        pumpMetrics.close();

        ProcessorMetrics processorMetrics = MetricsProvider.forProcessor(new NoOpProcessor());
        assertThat(processorMetrics.isEnabled()).isFalse();
        processorMetrics.close();

        SinkMetrics sinkMetrics = MetricsProvider.forSink(new BlackholeSink());
        assertThat(sinkMetrics.isEnabled()).isFalse();
        sinkMetrics.close();
    }
}