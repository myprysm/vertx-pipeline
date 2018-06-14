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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Function;

import static fr.myprysm.pipeline.validation.JsonValidation.ENV_PREFIX;
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
        info("[{}] Shutting down...", clazz);
        preShutdown().andThen(shutdown())
                .doOnComplete(this::logShutdown)
                .doOnError(this::logErrorShutdown)
                .onErrorComplete()
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
        info("[{}] Shutdown.", clazz);
    }

    private void logErrorShutdown(Throwable throwable) {
        error("An error occurred while closing [{}]", clazz);
        error("Reason: ", throwable);
        error("Shutting down anyway...");
    }

    protected Completable preShutdown() {
        return Completable.complete();
    }

    private void logErrorStart(Throwable throwable) {
        error("An error occured during deployment of [{}]", clazz);
        error("Configuration is [{}]", config());
        error("Reason: ", throwable);
    }

    private void logStarted() {
        info("[{}] Started.", clazz);
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
            delegate().error("Configuration invalid for component [{}]: {}", config.getValue("name", DEFAULT_NAME), result);
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
     * Returns the environment property as a string.
     *
     * @param environment the environment property
     * @return the value or null
     * @see #getEnv(String, Function)
     */
    protected String getEnvAsString(String environment) {
        return getEnv(environment, Function.identity());
    }

    /**
     * Returns the environment property as a boolean.
     *
     * @param environment the environment property
     * @return the value or null
     * @see #getEnv(String, Function)
     */
    protected Boolean getEnvAsBoolean(String environment) {
        return getEnv(environment, value -> {
            Boolean bool = BooleanUtils.toBooleanObject(value);
            return bool != null ? bool : false;
        });
    }

    /**
     * Returns the environment property as a integer.
     *
     * @param environment the environment property
     * @return the value or null
     * @see #getEnv(String, Function)
     */
    protected Integer getEnvAsInt(String environment) {
        return getEnv(environment, value -> parseNumber(value).map(Number::intValue).orElse(null));
    }

    /**
     * Returns the environment property as a long.
     *
     * @param environment the environment property
     * @return the value or null
     * @see #getEnv(String, Function)
     */
    protected Long getEnvAsLong(String environment) {
        return getEnv(environment, value -> parseNumber(value).map(Number::longValue).orElse(null));
    }

    /**
     * Returns the environment property as a float.
     *
     * @param environment the environment property
     * @return the value or null
     * @see #getEnv(String, Function)
     */
    protected Float getEnvAsFloat(String environment) {
        return getEnv(environment, value -> parseNumber(value).map(Number::floatValue).orElse(null));
    }

    /**
     * Returns the environment property as a double.
     *
     * @param environment the environment property
     * @return the value or null
     * @see #getEnv(String, Function)
     */
    protected Double getEnvAsDouble(String environment) {
        return getEnv(environment, value -> parseNumber(value).map(Number::doubleValue).orElse(null));
    }

    /**
     * Parses a number and returns a nullable optional.
     *
     * @param value the string to parse
     * @return the number if parseable
     */
    protected Optional<Number> parseNumber(String value) {
        return Optional.ofNullable(NumberUtils.createNumber(value));
    }

    /**
     * Get the environment property as the expected type.
     * <p>
     * Environment property expects to be prefixed with the ENV_PREFIX ("ENV:") to be effectively extracted.
     * Default value is extracted by suffixing the property name with "|" and adding that default.
     *
     * <code>ENV:some.environment.boolean|false</code>
     * <code>ENV:some.environment.string|some text</code>
     * <code>ENV:some.environment.integer|10</code>
     * <code>ENV:some.environment.long|10</code>
     * <code>ENV:some.environment.float|10.01</code>
     * <code>ENV:some.environment.double|10.01</code>
     * Please note that this method may return <code>null</code> values.
     *
     * @param environment the full environment property reference
     * @param mapper      the mapper to transform the string
     * @param <T>         the expected return type
     * @return the value or null
     */
    protected <T> T getEnv(String environment, Function<String, T> mapper) {
        Pair<String, String> parsed = parseEnvironment(environment);
        String value = System.getProperty(parsed.getKey());
        if (value == null) value = System.getenv(parsed.getKey());
        if (value == null) value = parsed.getValue();
        return value == null ? null : mapper.apply(value);
    }

    private Pair<String, String> parseEnvironment(String environment) {
        String start = environment.substring(ENV_PREFIX.length());
        return start.contains("|")
                ? Pair.of(start.substring(0, start.indexOf("|")), start.substring(start.indexOf("|") + 1))
                : Pair.of(start, null);
    }


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
            logger.trace("[{}] " + message, prepareArgs(name, args));
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
            logger.debug("[{}] " + message, prepareArgs(name, args));
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
            logger.info("[{}] " + message, prepareArgs(name, args));
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
            logger.warn("[{}] " + message, prepareArgs(name, args));
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
            logger.error("[{}] " + message, prepareArgs(name, args));
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

    private Object[] prepareArgs(String name, Object... args) {
        return ArrayUtils.addAll(new Object[]{name}, args);
    }
}

