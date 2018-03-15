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

import com.codahale.metrics.*;
import io.vertx.core.spi.metrics.Metrics;
import io.vertx.ext.dropwizard.ThroughputMeter;
import io.vertx.ext.dropwizard.ThroughputTimer;

/**
 * Base Codahale metrics object.
 *
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public abstract class AbstractMetrics implements Metrics {

    private final MetricRegistry registry;
    private final String baseName;

    AbstractMetrics(MetricRegistry registry, String baseName) {
        this.registry = registry;
        this.baseName = baseName;
    }

    private String nameOf(String... names) {
        return MetricRegistry.name(baseName, names);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    protected <T> Gauge<T> gauge(Gauge<T> gauge, String... names) {
        try {
            return registry.register(nameOf(names), gauge);
        } catch (IllegalArgumentException e) {
            return gauge;
        }
    }

    protected Counter counter(String... names) {
        try {
            return registry.counter(nameOf(names));
        } catch (Exception e) {
            return new Counter();
        }
    }

    protected Histogram histogram(String... names) {
        try {
            return registry.histogram(nameOf(names));
        } catch (Exception e) {
            return new Histogram(new ExponentiallyDecayingReservoir());
        }
    }

    protected Meter meter(String... names) {
        try {
            return registry.meter(nameOf(names));
        } catch (Exception e) {
            return new Meter();
        }
    }

    protected Timer timer(String... names) {
        try {
            return registry.timer(nameOf(names));
        } catch (Exception e) {
            return new Timer();
        }
    }

    protected ThroughputMeter throughputMeter(String... names) {
        Metric metric = registry.getMetrics().get(nameOf(names));
        if (metric == null) {
            return registry.register(nameOf(names), new ThroughputMeter());
        } else if (metric instanceof ThroughputMeter) {
            return (ThroughputMeter) metric;
        } else {
            return new ThroughputMeter();
        }
    }

    protected ThroughputTimer throughputTimer(String... names) {
        Metric metric = registry.getMetrics().get(nameOf(names));
        if (metric == null) {
            return registry.register(nameOf(names), new ThroughputTimer());
        } else if (metric instanceof ThroughputTimer) {
            return (ThroughputTimer) metric;
        } else {
            return new ThroughputTimer();
        }
    }

    protected void remove(String... names) {
        registry.remove(nameOf(names));
    }

    protected void removeAll() {
        registry.removeMatching((name, metric) -> name.startsWith(baseName));
    }
}
