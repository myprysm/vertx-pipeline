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

package fr.myprysm.pipeline.datasource;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.CompletableHelper;
import io.vertx.reactivex.SingleHelper;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.shareddata.AsyncMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

class DatasourceRegistryImpl implements DatasourceRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(DatasourceRegistryImpl.class);
    private static final String REGISTRY_NAME = "fr.myprysm.pipeline.datasource.registry";
    private static final String CONFIGURATION_NAME = "fr.myprysm.pipeline.datasource.configuration";
    private final Vertx vertx;
    private AsyncMap<String, JsonArray> registrations;
    private AsyncMap<String, JsonObject> configurations;
    private final String deployment;

    public DatasourceRegistryImpl(io.vertx.core.Vertx vertx, String deployment, Handler<AsyncResult<DatasourceRegistry>> handler) {
        this.deployment = deployment;
        this.vertx = new Vertx(vertx);
        this.vertx.eventBus().getDelegate().registerDefaultCodec(DatasourceRegistryException.class, new DatasourceRegistryExceptionMessageCodec());
        Single.zip(
                this.vertx.sharedData().<String, JsonArray>rxGetAsyncMap(REGISTRY_NAME),
                this.vertx.sharedData().<String, JsonObject>rxGetAsyncMap(CONFIGURATION_NAME),
                (reg, cnf) -> {
                    registrations = reg;
                    configurations = cnf;
                    return this;
                }
        ).subscribe(SingleHelper.toObserver(handler));
    }

    @Override
    public DatasourceRegistry getConfiguration(String name, Handler<AsyncResult<DatasourceConfiguration>> handler) {
        configurations.rxGet(name)
                .flatMap(this.configNotNull(name))
                .subscribe(SingleHelper.toObserver(handler));
        return this;
    }

    @Override
    public DatasourceRegistry storeConfiguration(DatasourceConfiguration configuration, Handler<AsyncResult<Void>> handler) {
        configurations.rxPutIfAbsent(configuration.getName(), configuration.toJson())
                .flatMapCompletable(config -> {
                    if (config != null) {
                        return Completable.error(new DatasourceRegistryException(DatasourceRegistryException.CONFIG_NAME_CONFLICT, "Configuration " + configuration.getName() + " is already registered"));
                    }

                    return Completable.complete();
                }).subscribe(CompletableHelper.toObserver(handler));
        return this;
    }

    @Override
    public DatasourceRegistry removeConfiguration(String name, Handler<AsyncResult<DatasourceConfiguration>> handler) {
        configurations.rxRemove(name)
                .flatMap(this.configNotNull(name))
                .subscribe(SingleHelper.toObserver(handler));
        return this;
    }

    private Function<JsonObject, Single<DatasourceConfiguration>> configNotNull(String name) {
        return config -> {
            if (config == null) {
                return Single.error(new DatasourceRegistryException(DatasourceRegistryException.CONFIG_NOT_FOUND, "Could not find configuration for name '" + name + "'"));
            }

            return Single.just(new DatasourceConfiguration(config));
        };
    }

    @Override
    public DatasourceRegistry registerComponent(DatasourceRegistration registration, Handler<AsyncResult<Boolean>> handler) {
        registrations.rxPutIfAbsent(registration.getDeployment(), new JsonArray())
                .toCompletable()
                .andThen(registrations.rxGet(registration.getDeployment()))
                .map(this.checkAdd(registration))
                .doOnSuccess(added -> {
                    if (added) {
                        LOG.info("Registered datasource: {}", registration);
                    } else {
                        LOG.info("{} already registered on {}", registration.getComponent(), registration.getDeployment());
                    }
                })
                .subscribe(SingleHelper.toObserver(handler));
        return this;
    }

    private Function<JsonArray, Boolean> checkAdd(DatasourceRegistration registration) {
        return array -> {
            boolean contains = array.stream()
                    .map(JsonObject.class::cast)
                    .anyMatch(registration.toJson()::equals);

            if (!contains) {
                array.add(registration.toJson());
            }

            return !contains;
        };
    }


    @Override
    public DatasourceRegistry unregisterComponent(DatasourceRegistration registration, Handler<AsyncResult<Boolean>> handler) {
        registrations.rxGet(registration.getDeployment())
                .map(list -> list.remove(registration.toJson()))
                .onErrorResumeNext(throwable -> Single.error(new DatasourceRegistryException(DatasourceRegistryException.DEPLOYMENT_NOT_FOUND, "Could not find deployment '" + registration.getDeployment() + "'")))
                .subscribe(SingleHelper.toObserver(handler));
        return this;
    }

    @Override
    public DatasourceRegistry registrationsForDeployment(String deployment, Handler<AsyncResult<List<DatasourceRegistration>>> handler) {
        registrations.rxGet(deployment)
                .flatMap(deps -> {
                    if (deps == null) {
                        return Single.error(new DatasourceRegistryException(DatasourceRegistryException.DEPLOYMENT_NOT_FOUND, "Could not find deployment '" + deployment + "'"));
                    }

                    return Single.just(deps.stream().map(JsonObject.class::cast).map(DatasourceRegistration::new).collect(toList()));
                }).subscribe(SingleHelper.toObserver(handler));
        return this;
    }


    @Override
    public DatasourceRegistry registrationsForComponent(String component, Handler<AsyncResult<List<DatasourceRegistration>>> handler) {
        registrationsFor(component, json -> json.getString("component"), handler);
        return this;
    }

    @Override
    public DatasourceRegistry registrationsForAlias(String alias, Handler<AsyncResult<List<DatasourceRegistration>>> handler) {
        registrationsFor(alias, json -> json.getString("alias"), handler);
        return this;
    }

    @Override
    public DatasourceRegistry registrationsForConfiguration(String configuration, Handler<AsyncResult<List<DatasourceRegistration>>> handler) {
        registrationsFor(configuration, json -> json.getString("configuration"), handler);
        return this;
    }

    @SuppressWarnings("unchecked")
    private void registrationsFor(String value, java.util.function.Function<JsonObject, String> valueSupplier, Handler<AsyncResult<List<DatasourceRegistration>>> handler) {
        registrations.getDelegate().values(rawAr -> {
            AsyncResult<List<JsonArray>> ar = (AsyncResult<List<JsonArray>>) rawAr;
            if (ar.succeeded()) {
                List<JsonArray> regs = ar.result();
                List<DatasourceRegistration> registrationList = regs.stream()
                        .flatMap(JsonArray::stream)
                        .map(JsonObject.class::cast)
                        .filter(registration -> valueSupplier.apply(registration).equals(value))
                        .map(DatasourceRegistration::new)
                        .collect(toList());

                handler.handle(Future.succeededFuture(registrationList));
            } else {
                handler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void close(Handler<AsyncResult<Void>> handler) {
        // Remove all registrations.
        // Just logs error when already cleaned up...
        Completable unregistrations = registrations.rxRemove(deployment)
                .flatMapObservable(Observable::fromIterable)
                .flatMapCompletable(reg -> {
                    LOG.info("unregistering datasource: {}", reg);
                    return Completable.complete();
                })
                .onErrorResumeNext(throwable -> {
                    LOG.error("No component registered for deployment[{}]", deployment);
                    return Completable.complete();
                });

        // Prepare to remove each configuration bound to the deployment id
        List<Completable> unregs = new ArrayList<>();
        Completable unconfigurations = Completable.create(emitter -> {
            configurations.getDelegate().entries(rawAr -> {
                AsyncResult<Map<String, JsonObject>> ar = (AsyncResult<Map<String, JsonObject>>) rawAr;
                if (ar.succeeded()) {
                    ar.result().entrySet()
                            .stream()
                            .filter(entry -> entry.getValue().getString("deployment").equals(deployment))
                            .forEach(entry -> unregs.add(configurations.rxRemove(entry.getKey())
                                    .doOnSuccess(config -> LOG.info("Removed datasource configuration for {}", config.getString("name")))
                                    .toCompletable()
                            ));
                } else {
                    LOG.error("Unable to get datasource configurations: ", ar.cause());
                }
                emitter.onComplete();
            });
        });

        unregistrations
                .andThen(unconfigurations)
                .andThen(Completable.concat(unregs))
                .subscribe(CompletableHelper.toObserver(handler));

    }
}
