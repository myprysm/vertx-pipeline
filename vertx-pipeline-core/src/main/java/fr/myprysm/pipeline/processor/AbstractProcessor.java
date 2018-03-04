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

package fr.myprysm.pipeline.processor;


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

    @Override
    protected ValidationResult preValidate(JsonObject config) {
        return ProcessorOptionsValidation.validate(config);
    }

    @Override
    protected JsonObject preConfiguration(JsonObject config) {
        ProcessorOptions options = new ProcessorOptions(config);
        exchange = new ExchangeOptions(config);
        from = exchange.getFrom();
        recipients = exchange.getTo();
        to = RoundRobin.of(recipients).iterator();
        eventBus = vertx.eventBus();
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

    @Override
    public String to() {
        return to.next();
    }

    @Override
    public String from() {
        return from;
    }

    @Override
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

    @Override
    public void consume(Message<I> item) {
        I input = item.body();
        LOG.debug("[{}] Message received: {}", name(), input);

        try {
            transform(input).subscribe(this::publish);
        } catch (Exception e) {
            error("An error occurred while processing item: ", e);
        }
    }

    @Override
    public void publish(O item) {
        LOG.debug("[{}] Emitting message: {}", name(), item);
        eventBus().send(to(), item);
    }

    @Override
    protected Logger delegate() {
        return LOG;
    }
}
