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

package fr.myprysm.pipeline.processor;


import fr.myprysm.pipeline.metrics.MetricsProvider;
import fr.myprysm.pipeline.metrics.ProcessorMetrics;
import fr.myprysm.pipeline.pipeline.ExchangeOptions;
import fr.myprysm.pipeline.util.ConfigurableVerticle;
import fr.myprysm.pipeline.util.RoundRobin;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * Base of a processor.
 * It ensures that the configuration is valid, configures the processor and finally binds to the {@link EventBus}
 *
 * @param <I> The type of input items
 * @param <O> The type of output items
 */
abstract class AbstractProcessor<I, O, T extends ProcessorOptions> extends ConfigurableVerticle<T> implements Processor<I, O> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractProcessor.class);

    private ExchangeOptions exchange;
    private List<String> recipients;
    private Iterator<String> to;
    private MessageConsumer<I> consumer;
    private EventBus eventBus;
    private String from;
    private ProcessorMetrics metrics;

    @Override
    protected ValidationResult preValidate(JsonObject config) {
        return ProcessorOptionsValidation.validate(config);
    }

    @Override
    protected JsonObject preConfiguration(JsonObject config) {
        exchange = new ExchangeOptions(config);
        from = exchange.getFrom();
        recipients = exchange.getTo();
        to = RoundRobin.of(recipients).iterator();
        eventBus = vertx.eventBus();
        metrics = MetricsProvider.forProcessor(this);
        return config;
    }

    @Override
    protected Completable postStartVerticle() {
        consumer = eventBus.consumer(this.from, this::consume);
        return Completable.complete();
    }

    @Override
    protected Completable preShutdown() {
        return consumer.rxUnregister();
    }


    /**
     * Provides the address to publish an item
     *
     * @return the address to publish.
     */
    public String to() {
        return to.next();
    }

    /**
     * Provides the address this processor listens to.
     *
     * @return the address.
     */
    public String from() {
        return from;
    }

    /**
     * Address list of this processor.
     *
     * @return the address list of this processor.
     */
    public List<String> recipients() {
        return recipients;
    }

    @Override
    public EventBus eventBus() {
        return eventBus;
    }

    @Override
    public ExchangeOptions exchange() {
        return exchange;
    }

    private void consume(Message<I> item) {
        metrics.eventReceived();
        I input = item.body();
        LOG.debug("[{}] Message received: {}", name(), input);

        try {
            transform(input).subscribe(this::publish, throwable -> this.handleInternalError(item, throwable));
        } catch (Exception e) {
            handleInternalError(item, e);
            error("An error occurred while processing item: ", e);
        }
    }

    private void handleInternalError(Message<I> item, Throwable throwable) {
        metrics.eventError();
        this.handleError(item, throwable);
    }


    /**
     * Delegate to handle errors.
     * <p>
     * Default behaviour is to log as <code>INFO</code> all errors flagged as <code>DiscardableEventException</code>,
     * as <code>ERROR</code> anything else.
     * <p>
     * Through this method you can handle the errors of all your processors as you want.
     *
     * @param item      the message that provoked the error
     * @param throwable the error
     */
    protected void handleError(Message<I> item, Throwable throwable) {
        if (throwable instanceof DiscardableEventException) {
            if (LOG.isInfoEnabled()) {
                info("Discarding event: {}", ((DiscardableEventException) throwable).getEvent());
            }
        } else {
            error("An error occurred while processing item: ", throwable);
        }
    }

    private void publish(O item) {
        LOG.debug("[{}] Emitting message: {}", name(), item);
        eventBus().send(to(), item);
        metrics.eventSent();
    }

    @Override
    protected Logger delegate() {
        return LOG;
    }
}
