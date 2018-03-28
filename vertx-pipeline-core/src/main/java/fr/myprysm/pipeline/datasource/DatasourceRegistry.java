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

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.List;

@ProxyGen
@VertxGen
public interface DatasourceRegistry {
    String ADDRESS = "datasource.registry";

    /**
     * Retrieve the datasource configuration identified by the name.
     *
     * @param name    the name of the datasource configuration
     * @param handler the result handler
     * @return this
     */
    @Fluent
    DatasourceRegistry getConfiguration(String name, Handler<AsyncResult<DatasourceConfiguration>> handler);

    /**
     * Stores a datasource configuration.
     * <p>
     * Datasource configuration are identified with their name.
     * <p>
     * If you try to add an already existing datasource, the handler will receive a {@link DatasourceRegistryException}.
     *
     * @param configuration the configuration to store
     * @param handler       the result handler
     * @return this
     */
    @Fluent
    DatasourceRegistry storeConfiguration(DatasourceConfiguration configuration, Handler<AsyncResult<Void>> handler);

    /**
     * Removes a datasource configuration from its name.
     * <p>
     * Datasource configuration are identified with their name.
     * <p>
     * If you try to add an already existing datasource, the handler will receive a {@link DatasourceRegistryException}.
     *
     * @param name the name of the configuration to remove
     * @param handler       the result handler
     * @return this
     */
    @Fluent
    DatasourceRegistry removeConfiguration(String name, Handler<AsyncResult<DatasourceConfiguration>> handler);

    /**
     * Registers a datasource on the service.
     * <p>
     * Note that a single component can be registered once on a deployment.
     * <p>
     * As duplicate declarations may be an error but will cause no trouble (component is already registered on deployment),
     * the handler will just be informed of the success of the operation.
     *
     * @param registration the registration
     * @param handler      the result handler
     * @return this
     */
    @Fluent
    DatasourceRegistry registerComponent(DatasourceRegistration registration, Handler<AsyncResult<Boolean>> handler);

    /**
     * Unregisters a datasource of the service.
     * <p>
     * The handler will be informed of the success of the operation.
     *
     * @param registration the registration
     * @param handler      the result handler
     * @return this
     */
    @Fluent
    DatasourceRegistry unregisterComponent(DatasourceRegistration registration, Handler<AsyncResult<Boolean>> handler);

    /**
     * Retrieve the registrations for the given deployment id.
     *
     * @param deployment the deployment id
     * @param handler    the result handler
     * @return this
     */
    @Fluent
    DatasourceRegistry registrationsForDeployment(String deployment, Handler<AsyncResult<List<DatasourceRegistration>>> handler);

    /**
     * Retrieve the registrations for the given component class name.
     *
     * @param component the component class name
     * @param handler   the result handler
     * @return this
     */
    @Fluent
    DatasourceRegistry registrationsForComponent(String component, Handler<AsyncResult<List<DatasourceRegistration>>> handler);

    /**
     * Retrieve the registrations for the given alias.
     *
     * @param alias   the alias
     * @param handler the result handler
     * @return this
     */
    @Fluent
    DatasourceRegistry registrationsForAlias(String alias, Handler<AsyncResult<List<DatasourceRegistration>>> handler);

    /**
     * Retrieve the registrations for the given configuration class name.
     *
     * @param configuration the configuration class name.
     * @param handler       the result handler
     * @return this
     */
    @Fluent
    DatasourceRegistry registrationsForConfiguration(String configuration, Handler<AsyncResult<List<DatasourceRegistration>>> handler);

    @GenIgnore
    static DatasourceRegistry create(Vertx vertx, String deployment, Handler<AsyncResult<DatasourceRegistry>> handler) {
        return new DatasourceRegistryImpl(vertx, deployment, handler);
    }

    @GenIgnore
    static fr.myprysm.pipeline.reactivex.datasource.DatasourceRegistry createRx(Vertx vertx, String deployment, Handler<AsyncResult<DatasourceRegistry>> handler) {
        return new fr.myprysm.pipeline.reactivex.datasource.DatasourceRegistry(create(vertx, deployment, handler));
    }

    @GenIgnore
    static DatasourceRegistry createProxy(Vertx vertx, String address) {
        return new DatasourceRegistryVertxEBProxy(vertx, address);
    }

    @GenIgnore
    static fr.myprysm.pipeline.reactivex.datasource.DatasourceRegistry createRxProxy(Vertx vertx, String address) {
        return new fr.myprysm.pipeline.reactivex.datasource.DatasourceRegistry(createProxy(vertx, address));
    }
}
