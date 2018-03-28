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

package fr.myprysm.pipeline.pump;

import fr.myprysm.pipeline.datasource.DatasourceConfig;
import fr.myprysm.pipeline.datasource.DatasourceConfiguration;
import fr.myprysm.pipeline.util.Alias;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.TimeUnit;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;

@DatasourceConfig(configClass = DatasourceConfiguration.class)
@Alias(prefix = "pipeline-playground", name = "dummy-ds-pump")
public class DummyDatasourcePump extends BaseDatasourcePump<DatasourcePumpOptions> {
    @Override
    public Flowable<JsonObject> pump() {
        return Flowable.timer(100, TimeUnit.MILLISECONDS)
                .map(tick -> obj().put("tick", tick));
    }

    @Override
    protected Completable startVerticle() {
        return Completable.complete();
    }

    @Override
    public DatasourcePumpOptions readConfiguration(JsonObject config) {
        return new DatasourcePumpOptions(config);
    }

    @Override
    public Completable configure(DatasourcePumpOptions config) {
        return Completable.complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return ValidationResult.valid();
    }
}
