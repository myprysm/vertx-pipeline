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

import fr.myprysm.pipeline.util.Alias;
import fr.myprysm.pipeline.util.ClasspathHelpers;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.reactivex.CompletableHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static strman.Strman.toKebabCase;

/**
 * Loads all the available datasource from the classes of this instance into
 * the {@link DatasourceRegistry}.
 */
public class DatasourceLoader {
    private static final Logger LOG = LoggerFactory.getLogger(DatasourceLoader.class);
    fr.myprysm.pipeline.reactivex.datasource.DatasourceRegistry registry;
    String deploymentId;

    public DatasourceLoader(DatasourceRegistry registry, String deploymentId) {
        this.registry = new fr.myprysm.pipeline.reactivex.datasource.DatasourceRegistry(registry);
        this.deploymentId = deploymentId;
    }

    public DatasourceLoader load(Handler<AsyncResult<Void>> handler) {
        Observable.fromIterable(ClasspathHelpers.toClasses(ClasspathHelpers.getDatasourceComponentClassNames()))
                .map(this::prepareRegistration)
                .flatMapCompletable(this::register)
                .subscribe(CompletableHelper.toObserver(handler));

        return this;
    }

    private Completable register(DatasourceRegistration datasourceRegistration) {
        return registry.rxRegisterComponent(datasourceRegistration)
                .flatMapCompletable(registered -> {
                    if (!registered) {
                        LOG.info("Couldn't register {}", datasourceRegistration);
                    }
                    return Completable.complete();
                });
    }

    private DatasourceRegistration prepareRegistration(Class<?> component) {
        DatasourceConfig aConfig = component.getAnnotation(DatasourceConfig.class);
        Class<? extends DatasourceConfiguration> configClass = aConfig.configClass();
        String alias = extractAlias(configClass);

        return new DatasourceRegistration()
                .setDeployment(deploymentId)
                .setComponent(component.getName())
                .setAlias(alias)
                .setConfiguration(configClass.getName());
    }

    private String extractAlias(Class<? extends DatasourceConfiguration> configClass) {
        Alias anAlias = configClass.getAnnotation(Alias.class);
        String alias;
        if (anAlias != null) {
            alias = aliasFromAnnotation(anAlias);
        } else {
            alias = aliasFromClass(configClass);
        }

        return alias;
    }

    private String aliasFromClass(Class<? extends DatasourceConfiguration> configClass) {
        return ("datasource." + toKebabCase(configClass.getSimpleName()));
    }

    private String aliasFromAnnotation(Alias anAlias) {
        return (anAlias.prefix() + '.' + anAlias.name()).toLowerCase();
    }
}
