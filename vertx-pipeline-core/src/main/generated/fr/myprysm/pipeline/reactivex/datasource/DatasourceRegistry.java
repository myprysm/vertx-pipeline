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

package fr.myprysm.pipeline.reactivex.datasource;

import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import java.util.List;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import fr.myprysm.pipeline.datasource.DatasourceRegistration;
import fr.myprysm.pipeline.datasource.DatasourceConfiguration;


@io.vertx.lang.reactivex.RxGen(fr.myprysm.pipeline.datasource.DatasourceRegistry.class)
public class DatasourceRegistry {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DatasourceRegistry that = (DatasourceRegistry) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final io.vertx.lang.reactivex.TypeArg<DatasourceRegistry> __TYPE_ARG = new io.vertx.lang.reactivex.TypeArg<>(
    obj -> new DatasourceRegistry((fr.myprysm.pipeline.datasource.DatasourceRegistry) obj),
    DatasourceRegistry::getDelegate
  );

  private final fr.myprysm.pipeline.datasource.DatasourceRegistry delegate;
  
  public DatasourceRegistry(fr.myprysm.pipeline.datasource.DatasourceRegistry delegate) {
    this.delegate = delegate;
  }

  public fr.myprysm.pipeline.datasource.DatasourceRegistry getDelegate() {
    return delegate;
  }

  /**
   * Retrieve the datasource configuration identified by the name.
   * @param name the name of the datasource configuration
   * @param handler the result handler
   * @return this
   */
  public DatasourceRegistry getConfiguration(String name, Handler<AsyncResult<DatasourceConfiguration>> handler) { 
    delegate.getConfiguration(name, handler);
    return this;
  }

  /**
   * Retrieve the datasource configuration identified by the name.
   * @param name the name of the datasource configuration
   * @return 
   */
  public Single<DatasourceConfiguration> rxGetConfiguration(String name) { 
    return new io.vertx.reactivex.core.impl.AsyncResultSingle<DatasourceConfiguration>(handler -> {
      getConfiguration(name, handler);
    });
  }

  /**
   * Stores a datasource configuration.
   * <p>
   * Datasource configuration are identified with their name.
   * <p>
   * If you try to add an already existing datasource, the handler will receive a {@link fr.myprysm.pipeline.reactivex.datasource.DatasourceRegistryException}.
   * @param configuration the configuration to store
   * @param handler the result handler
   * @return this
   */
  public DatasourceRegistry storeConfiguration(DatasourceConfiguration configuration, Handler<AsyncResult<Void>> handler) { 
    delegate.storeConfiguration(configuration, handler);
    return this;
  }

  /**
   * Stores a datasource configuration.
   * <p>
   * Datasource configuration are identified with their name.
   * <p>
   * If you try to add an already existing datasource, the handler will receive a {@link fr.myprysm.pipeline.reactivex.datasource.DatasourceRegistryException}.
   * @param configuration the configuration to store
   * @return 
   */
  public Completable rxStoreConfiguration(DatasourceConfiguration configuration) { 
    return new io.vertx.reactivex.core.impl.AsyncResultCompletable(handler -> {
      storeConfiguration(configuration, handler);
    });
  }

  /**
   * Registers a datasource on the service.
   * <p>
   * Note that a single component can be registered once on a deployment.
   * <p>
   * As duplicate declarations may be an error but will cause no trouble (component is already registered on deployment),
   * the handler will just be informed of the success of the operation.
   * @param registration the registration
   * @param handler the result handler
   * @return this
   */
  public DatasourceRegistry registerComponent(DatasourceRegistration registration, Handler<AsyncResult<Boolean>> handler) { 
    delegate.registerComponent(registration, handler);
    return this;
  }

  /**
   * Registers a datasource on the service.
   * <p>
   * Note that a single component can be registered once on a deployment.
   * <p>
   * As duplicate declarations may be an error but will cause no trouble (component is already registered on deployment),
   * the handler will just be informed of the success of the operation.
   * @param registration the registration
   * @return 
   */
  public Single<Boolean> rxRegisterComponent(DatasourceRegistration registration) { 
    return new io.vertx.reactivex.core.impl.AsyncResultSingle<Boolean>(handler -> {
      registerComponent(registration, handler);
    });
  }

  /**
   * Unregisters a datasource of the service.
   * <p>
   * The handler will be informed of the success of the operation.
   * @param registration the registration
   * @param handler the result handler
   * @return this
   */
  public DatasourceRegistry unregisterComponent(DatasourceRegistration registration, Handler<AsyncResult<Boolean>> handler) { 
    delegate.unregisterComponent(registration, handler);
    return this;
  }

  /**
   * Unregisters a datasource of the service.
   * <p>
   * The handler will be informed of the success of the operation.
   * @param registration the registration
   * @return 
   */
  public Single<Boolean> rxUnregisterComponent(DatasourceRegistration registration) { 
    return new io.vertx.reactivex.core.impl.AsyncResultSingle<Boolean>(handler -> {
      unregisterComponent(registration, handler);
    });
  }

  /**
   * Retrieve the registrations for the given deployment id.
   * @param deployment the deployment id
   * @param handler the result handler
   * @return this
   */
  public DatasourceRegistry registrationsForDeployment(String deployment, Handler<AsyncResult<List<DatasourceRegistration>>> handler) { 
    delegate.registrationsForDeployment(deployment, handler);
    return this;
  }

  /**
   * Retrieve the registrations for the given deployment id.
   * @param deployment the deployment id
   * @return 
   */
  public Single<List<DatasourceRegistration>> rxRegistrationsForDeployment(String deployment) { 
    return new io.vertx.reactivex.core.impl.AsyncResultSingle<List<DatasourceRegistration>>(handler -> {
      registrationsForDeployment(deployment, handler);
    });
  }

  /**
   * Retrieve the registrations for the given component class name.
   * @param component the component class name
   * @param handler the result handler
   * @return this
   */
  public DatasourceRegistry registrationsForComponent(String component, Handler<AsyncResult<List<DatasourceRegistration>>> handler) { 
    delegate.registrationsForComponent(component, handler);
    return this;
  }

  /**
   * Retrieve the registrations for the given component class name.
   * @param component the component class name
   * @return 
   */
  public Single<List<DatasourceRegistration>> rxRegistrationsForComponent(String component) { 
    return new io.vertx.reactivex.core.impl.AsyncResultSingle<List<DatasourceRegistration>>(handler -> {
      registrationsForComponent(component, handler);
    });
  }

  /**
   * Retrieve the registrations for the given alias.
   * @param alias the alias
   * @param handler the result handler
   * @return this
   */
  public DatasourceRegistry registrationsForAlias(String alias, Handler<AsyncResult<List<DatasourceRegistration>>> handler) { 
    delegate.registrationsForAlias(alias, handler);
    return this;
  }

  /**
   * Retrieve the registrations for the given alias.
   * @param alias the alias
   * @return 
   */
  public Single<List<DatasourceRegistration>> rxRegistrationsForAlias(String alias) { 
    return new io.vertx.reactivex.core.impl.AsyncResultSingle<List<DatasourceRegistration>>(handler -> {
      registrationsForAlias(alias, handler);
    });
  }

  /**
   * Retrieve the registrations for the given configuration class name.
   * @param configuration the configuration class name.
   * @param handler the result handler
   * @return this
   */
  public DatasourceRegistry registrationsForConfiguration(String configuration, Handler<AsyncResult<List<DatasourceRegistration>>> handler) { 
    delegate.registrationsForConfiguration(configuration, handler);
    return this;
  }

  /**
   * Retrieve the registrations for the given configuration class name.
   * @param configuration the configuration class name.
   * @return 
   */
  public Single<List<DatasourceRegistration>> rxRegistrationsForConfiguration(String configuration) { 
    return new io.vertx.reactivex.core.impl.AsyncResultSingle<List<DatasourceRegistration>>(handler -> {
      registrationsForConfiguration(configuration, handler);
    });
  }


  public static  DatasourceRegistry newInstance(fr.myprysm.pipeline.datasource.DatasourceRegistry arg) {
    return arg != null ? new DatasourceRegistry(arg) : null;
  }
}
