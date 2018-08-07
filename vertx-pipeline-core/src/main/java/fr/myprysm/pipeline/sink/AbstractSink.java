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

package fr.myprysm.pipeline.sink;

import fr.myprysm.pipeline.metrics.MetricsProvider;
import fr.myprysm.pipeline.metrics.SinkMetrics;
import fr.myprysm.pipeline.pipeline.ExchangeOptions;
import fr.myprysm.pipeline.util.ConfigurableVerticle;
import fr.myprysm.pipeline.util.Named;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract sink that receives events in a pipeline over Vertx event bus.
 * <p>
 * This class provides the necessary pieces over the {@link ConfigurableVerticle}
 * to focus {@link Sink} development on the logic.
 *
 * @param <I> The events input type
 * @param <T> The options type
 */
abstract class AbstractSink<I, T extends SinkOptions> extends ConfigurableVerticle<T> implements Sink<I>, Named {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSink.class);

    /**
     * Vertx event bus.
     */
    private EventBus eventBus;

    /**
     * Exchange options.
     */
    private ExchangeOptions exchange;

    /**
     * Event source address.
     */
    private String from;

    /**
     * Message consumer for incoming events.
     */
    private MessageConsumer<I> consumer;

    /**
     * Metrics handler.
     */
    private SinkMetrics metrics;

    @Override
    protected ValidationResult preValidate(JsonObject json) {
        return SinkOptionsValidation.validate(json);
    }

    @Override
    protected JsonObject preConfiguration(JsonObject json) {
        exchange = new ExchangeOptions(json);
        eventBus = vertx.eventBus();
        from = exchange.getFrom();
        metrics = MetricsProvider.forSink(this);
        return json;
    }

    @Override
    protected Completable postStartVerticle() {
        consumer = eventBus().<I>consumer(from()).handler(this::consume);
        return Completable.complete();
    }


    @Override
    protected Completable preShutdown() {
        return consumer.rxUnregister();
    }

    /**
     * Provides the address this sink listens to.
     *
     * @return the address.
     */
    public String from() {
        return from;
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
     * Consumes the incoming event and send it for {@link #drain(I)} to the component.
     *
     * @param item the message event
     */
    private void consume(Message<I> item) {
        metrics.eventReceived();
        I input = item.body();
        LOG.debug("Message received: {}", input);
        try {
            drain(input);
        } catch (Exception exc) {
            handleInternalError(item, exc);
        }
    }

    /**
     * Handles error metric and delegate error processing.
     *
     * @param item the message that triggered the error
     * @param exc  the error
     */
    private void handleInternalError(Message<I> item, Exception exc) {
        metrics.eventError();
        handleError(item, exc);
    }

    /**
     * Delegate to handle errors properly.
     * <p>
     * Default behaviour is to log as <code>ERROR</code> anything.
     * <p>
     * Through this method you can handle the errors of all your sinks.
     *
     * @param item      the message that provoked the error
     * @param throwable the error
     */
    protected void handleError(Message<I> item, Throwable throwable) {

        if (item.body() != null) {
            error("An error occured while draining item {}", item.body());
            error("Error is: ", throwable);
        } else {
            error("An error occured while draining item.", throwable);
        }

    }

    @Override
    protected Logger delegate() {
        return LOG;
    }
}
