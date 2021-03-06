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

package fr.myprysm.pipeline.util;

import fr.myprysm.pipeline.validation.Validable;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.CompletableHelper;
import io.vertx.reactivex.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static fr.myprysm.pipeline.validation.ValidationResult.valid;

/**
 * Configurable {@link Verticle} that provides all the necessary tooling
 * to quickly build a new {@link Verticle} implementation.
 *
 * @param <O> the type of options this {@link ConfigurableVerticle} holds
 */
public abstract class ConfigurableVerticle<O extends Options> extends AbstractVerticle implements Configurable<O>, Validable, ShutdownHook, Named {
    public static final String DEFAULT_NAME = "configurable-verticle";
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableVerticle.class);

    private final String clazz = getClass().getCanonicalName();
    private String name = DEFAULT_NAME;
    private O config;

    @Override
    public void start(Future<Void> start) {
        LOG.info("[{}] Starting...", clazz);
        doValidate(config())
                .map(this::setName)
                .map(this::preConfiguration)
                .map(this::readConfiguration)
                .map(this::setConfiguration)
                .flatMapCompletable(this::configure)
                .andThen(Completable.defer(this::startVerticle))
                .andThen(Completable.defer(this::postStartVerticle))
                .doOnComplete(this::logStarted)
                .doOnError(this::logErrorStart)
                .subscribe(CompletableHelper.toObserver(start));
    }

    /**
     * Sets the config internally
     *
     * @param config the configuration
     * @return the configuration
     */
    private O setConfiguration(O config) {
        this.config = config;
        return config;
    }

    /**
     * Get the configuration object of this verticle
     * <p>
     * This will usually not be useful because of the {@link #configure(Options)} step
     * in the lifecycle but you still can access the configuration object through here.
     *
     * @return the configuration of this verticle
     */
    protected O configuration() {
        return config;
    }

    /**
     * Sets the name of this verticle from the configuration.
     *
     * @param jsonObject the configuration
     * @return the configuration.
     */
    private JsonObject setName(JsonObject jsonObject) {
        this.name = jsonObject.getString("name", DEFAULT_NAME);
        return jsonObject;
    }

    @Override
    public void stop(Future<Void> stop) throws Exception {
        LOG.info("[{}] Shutting down...", clazz);
        preShutdown().andThen(shutdown())
                .doOnComplete(this::logShutdown)
                .doOnError(this::logErrorShutdown)
                .subscribe(CompletableHelper.toObserver(stop));
    }

    /**
     * The name of this {@link ConfigurableVerticle}.
     *
     * @return the name of this verticle.
     */
    public String name() {
        return name;
    }

    private void logShutdown() {
        LOG.info("[{}] Shutdown.", clazz);
    }

    private void logErrorShutdown(Throwable throwable) {
        LOG.error("An error occurred while closing [{}]", clazz);
        LOG.error("Reason: ", throwable);
        LOG.error("Shutting down anyway...");
    }

    protected Completable preShutdown() {
        return Completable.complete();
    }

    private void logErrorStart(Throwable throwable) {
        LOG.error("An error occured during deployment of [{}]", clazz);
        LOG.error("Configuration is [{}]", config());
        LOG.error("Reason: ", throwable);
    }

    private void logStarted() {
        LOG.info("[{}] Started.", clazz);
    }


    /**
     * Pre-validation hook to validate the {@link ConfigurableVerticle} configuration
     *
     * @param config the configuration
     * @return the result.
     */
    protected ValidationResult preValidate(JsonObject config) {
        return valid();
    }

    private Single<JsonObject> doValidate(JsonObject config) {
        ValidationResult result = preValidate(config).and(supplyValidate(config));
        if (!result.isValid()) {
            LOG.error("Configuration invalid: {}", result);
        }
        return result.isValid() ? Single.just(config) : Single.error(result.toException());
    }

    /**
     * Pre-configuration hook that can be implemented before the configuration
     * is applied on the final {@link ConfigurableVerticle}
     *
     * @param config the configuration
     * @return the configuration
     */
    protected JsonObject preConfiguration(JsonObject config) {
        return config;
    }

    /**
     * Post-start hook that can be implemented after the final {@link ConfigurableVerticle}
     * is fully started.
     *
     * @return A completable that indicates whether the {@link ConfigurableVerticle} is started.
     */
    protected Completable postStartVerticle() {
        return Completable.complete();
    }


    /**
     * Starts the verticle once the configuration is applied.
     *
     * @return A {@link Completable} to indicates whether the <code>ConfigurableVerticle</code> is started
     */
    protected abstract Completable startVerticle();

    /**
     * Get the delegated {@link Logger} of this {@link ConfigurableVerticle} if any.
     *
     * @return the {@link Logger} if any, <code>null</code> otherwise.
     */
    protected abstract Logger delegate();

    /**
     * Get the logger for this verticle.
     * Allows delegating logging to another logger that the one configured on this {@link Verticle}.
     * <p>
     * If no
     *
     * @return the Logger from delegate if any, ConfigurableVerticle logger otherwise
     */
    private Logger getLogger() {
        Logger logger = delegate();
        if (logger == null) {
            logger = LOG;
        }

        return logger;
    }

    /**
     * Logs the message as <code>TRACE</code> with provided objects.
     *
     * @param message the message to log
     * @param args    the arguments to format the message.
     */
    protected void trace(String message, Object... args) {
        Logger logger = getLogger();
        if (logger.isTraceEnabled()) {
            logger.trace("[{}] " + message, name, args);
        }
    }


    /**
     * Logs the message as <code>TRACE</code> with provided error.
     *
     * @param message   the message to log
     * @param throwable the error to log
     */
    protected void trace(String message, Throwable throwable) {
        Logger logger = getLogger();
        if (logger.isTraceEnabled()) {
            logger.trace("[" + name + "] " + message, throwable);
        }
    }

    /**
     * Logs the message as <code>DEBUG</code> with provided objects.
     *
     * @param message the message to log
     * @param args    the arguments to format the message.
     */
    protected void debug(String message, Object... args) {
        Logger logger = getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] " + message, name, args);
        }
    }


    /**
     * Logs the message as <code>DEBUG</code> with provided error.
     *
     * @param message   the message to log
     * @param throwable the error to log
     */
    protected void debug(String message, Throwable throwable) {
        Logger logger = getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug("[" + name + "] " + message, throwable);
        }
    }

    /**
     * Logs the message as <code>INFO</code> with provided objects.
     *
     * @param message the message to log
     * @param args    the arguments to format the message.
     */
    protected void info(String message, Object... args) {
        Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            logger.info("[{}] " + message, name, args);
        }
    }

    /**
     * Logs the message as <code>INFO</code> with provided error.
     *
     * @param message   the message to log
     * @param throwable the error to log
     */
    protected void info(String message, Throwable throwable) {
        Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            logger.info("[" + name + "] " + message, throwable);
        }
    }

    /**
     * Logs the message as <code>WARN</code> with provided objects.
     *
     * @param message the message to log
     * @param args    the arguments to format the message.
     */
    protected void warn(String message, Object... args) {
        Logger logger = getLogger();
        if (logger.isWarnEnabled()) {
            logger.warn("[{}] " + message, name, args);
        }
    }


    /**
     * Logs the message as <code>WARN</code> with provided error.
     *
     * @param message   the message to log
     * @param throwable the error to log
     */
    protected void warn(String message, Throwable throwable) {
        Logger logger = getLogger();
        if (logger.isWarnEnabled()) {
            LOG.warn("[" + name + "] " + message, throwable);
        }
    }

    /**
     * Logs the message as <code>ERROR</code> with provided objects.
     *
     * @param message the message to log
     * @param args    the arguments to format the message.
     */
    protected void error(String message, Object... args) {
        Logger logger = getLogger();
        if (logger.isErrorEnabled()) {
            logger.error("[{}] " + message, name, args);
        }
    }


    /**
     * Logs the message as <code>ERROR</code> with provided error.
     *
     * @param message   the message to log
     * @param throwable the error to log
     */
    protected void error(String message, Throwable throwable) {
        Logger logger = getLogger();
        if (logger.isErrorEnabled()) {
            logger.error("[" + name + "] " + message, throwable);
        }
    }

}
