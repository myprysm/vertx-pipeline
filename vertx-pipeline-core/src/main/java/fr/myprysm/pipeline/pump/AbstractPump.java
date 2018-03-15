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

package fr.myprysm.pipeline.pump;

import fr.myprysm.pipeline.metrics.MetricsProvider;
import fr.myprysm.pipeline.metrics.PumpMetrics;
import fr.myprysm.pipeline.pipeline.ExchangeOptions;
import fr.myprysm.pipeline.util.ConfigurableVerticle;
import fr.myprysm.pipeline.util.RoundRobin;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

abstract class AbstractPump<O, T extends PumpOptions> extends ConfigurableVerticle<T> implements Pump<O> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPump.class);
    private String name;

    private ExchangeOptions exchange;
    private List<String> recipients;
    private Iterator<String> to;
    private EventBus eventBus;
    private Disposable source;
    private PumpMetrics metrics;

    @Override
    protected ValidationResult preValidate(JsonObject config) {
        return PumpOptionsValidation.validate(config);
    }

    /**
     * Configures the prerequisites for this {@link Pump} to work.
     * It binds the {@link #pump()} to the {@link EventBus}
     *
     * @param config the configuration
     * @return the configuration
     */
    @Override
    protected JsonObject preConfiguration(JsonObject config) {
        PumpOptions pump = new PumpOptions(config);
        exchange = new ExchangeOptions(config);
        name = pump.getName();
        recipients = exchange.getTo();
        to = RoundRobin.of(recipients).iterator();
        eventBus = vertx.eventBus();
        metrics = MetricsProvider.forPump(this);
        return config;
    }

    @Override
    protected Completable postStartVerticle() {
        source = pump().subscribe(this::publish, this::handleInternalError);
        return Completable.complete();
    }

    @Override
    protected Completable preShutdown() {
        source.dispose();
        return Completable.complete();
    }

    private void handleInternalError(Throwable throwable) {
        metrics.eventError();
        handleError(throwable);
    }

    /**
     * Delegate to handle errors properly.
     * <p>
     * Default behaviour is to log as <code>ERROR</code> anything.
     * <p>
     * Through this method you can handle the errors of all your pumps.
     *
     * @param throwable the error
     */
    protected void handleError(Throwable throwable) {
        LOG.error("[" + name() + "] encountered an error: ", throwable);

    }

    /**
     * The name of this pump.
     *
     * @return the name of this pump.
     */
    public String name() {
        return name;
    }

    /**
     * The address to send messages.
     *
     * @return the address to send messages.
     */
    public String to() {
        return to.next();
    }

    @Override
    public EventBus eventBus() {
        return eventBus;
    }

    @Override
    public ExchangeOptions exchange() {
        return exchange;
    }

    /**
     * Address list of this pump.
     *
     * @return the address list of this pump.
     */
    public List<String> recipients() {
        return recipients;
    }

    private void publish(O item) {
        debug("Sending item: {}", item.toString());
        eventBus().send(to(), item);
        metrics.eventSent();
    }


    @Override
    protected Logger delegate() {
        return LOG;
    }
}
