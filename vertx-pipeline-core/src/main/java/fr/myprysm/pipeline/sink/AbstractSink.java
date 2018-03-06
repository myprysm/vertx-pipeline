/*
 * Copyright 2018 the original author or the original authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.myprysm.pipeline.sink;

import fr.myprysm.pipeline.pipeline.ExchangeOptions;
import fr.myprysm.pipeline.util.ConfigurableVerticle;
import fr.myprysm.pipeline.util.Named;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractSink<I, T extends SinkOptions> extends ConfigurableVerticle<T> implements Sink<I>, Named {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSink.class);
    private String name;
    private EventBus eventBus;
    private ExchangeOptions exchange;
    private String from;
    private MessageConsumer<I> consumer;
    private Flowable<Message<I>> flowableConsumer;
    private Disposable subscriber;

    @Override
    protected ValidationResult preValidate(JsonObject config) {
        return SinkOptionsValidation.validate(config);
    }

    @Override
    protected JsonObject preConfiguration(JsonObject config) {
        SinkOptions options = new SinkOptions(config);
        exchange = new ExchangeOptions(config);
        name = options.getName();
        eventBus = vertx.eventBus();
        from = exchange.getFrom();
        return config;
    }

    @Override
    protected Completable postStartVerticle() {
        consumer = eventBus().consumer(from());
        flowableConsumer = consumer.toFlowable();
        subscriber = flowableConsumer.subscribe(this::consume, this::onMessageError);
        return Completable.complete();
    }

    /**
     * Prints the received error from the bus.
     * Should almost never happen.
     *
     * @param throwable the error thrown.
     */
    private void onMessageError(Throwable throwable) {
        error("An error occured when receiving message: ", throwable);
    }

    @Override
    protected Completable preShutdown() {
        subscriber.dispose();
        return consumer.rxUnregister();
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
     * The address to receive messages.
     *
     * @return the address to receive messages.
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

    @Override
    public void consume(Message<I> item) {
        I input = item.body();
        LOG.debug("Message received: {}", input);
        try {
            drain(input);
        } catch (Exception exc) {
            error("An error occured while draining item.", exc);
        }
    }

    @Override
    protected Logger delegate() {
        return LOG;
    }
}
