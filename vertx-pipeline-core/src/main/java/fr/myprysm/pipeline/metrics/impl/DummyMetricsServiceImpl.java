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

package fr.myprysm.pipeline.metrics.impl;

import fr.myprysm.pipeline.metrics.MetricsService;
import fr.myprysm.pipeline.metrics.ProcessorMetrics;
import fr.myprysm.pipeline.metrics.PumpMetrics;
import fr.myprysm.pipeline.metrics.SinkMetrics;
import fr.myprysm.pipeline.processor.Processor;
import fr.myprysm.pipeline.pump.Pump;
import fr.myprysm.pipeline.sink.Sink;
import io.vertx.core.json.JsonObject;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;

public class DummyMetricsServiceImpl implements MetricsService {
    @Override
    public ProcessorMetrics forProcessor(Processor processor) {
        return DummyProcessorMetricsImpl.INSTANCE;
    }

    @Override
    public SinkMetrics forSink(Sink sink) {
        return DummySinkMetricsImpl.INSTANCE;
    }

    @Override
    public PumpMetrics forPump(Pump pump) {
        return DummyPumpMetricsImpl.INSTANCE;
    }

    @Override
    public JsonObject metrics(String baseName) {
        return obj();
    }

    @Override
    public JsonObject metrics() {
        return obj();
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void close() {
// does nothing
    }

    private static class DummyProcessorMetricsImpl implements ProcessorMetrics {

        static final DummyProcessorMetricsImpl INSTANCE = new DummyProcessorMetricsImpl();

        private DummyProcessorMetricsImpl() {
        }

        @Override
        public void eventReceived() {

        }

        @Override
        public void eventSent() {

        }

        @Override
        public void eventError() {

        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void close() {
        }
    }

    private static class DummyPumpMetricsImpl implements PumpMetrics {

        static final DummyPumpMetricsImpl INSTANCE = new DummyPumpMetricsImpl();

        private DummyPumpMetricsImpl() {
        }


        @Override
        public void eventSent() {

        }

        @Override
        public void eventError() {

        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void close() {
        }
    }

    private static class DummySinkMetricsImpl implements SinkMetrics {

        static final DummySinkMetricsImpl INSTANCE = new DummySinkMetricsImpl();

        private DummySinkMetricsImpl() {
        }

        @Override
        public void eventReceived() {
        }

        @Override
        public void eventError() {

        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void close() {
        }
    }
}
