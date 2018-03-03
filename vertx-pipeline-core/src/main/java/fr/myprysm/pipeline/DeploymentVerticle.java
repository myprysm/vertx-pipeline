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

package fr.myprysm.pipeline;


import fr.myprysm.pipeline.pipeline.PipelineVerticle;
import fr.myprysm.pipeline.util.ClasspathHelpers;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.CompletableHelper;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Objects;

import static io.reactivex.Observable.fromIterable;

public class DeploymentVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(DeploymentVerticle.class);
    public static final String PIPELINE_VERTICLE = "fr.myprysm.pipeline.pipeline.PipelineVerticle";
    public static final String DEPLOYMENT_ERROR = "Unable to deploy pipeline";

    private LinkedList<Pair<String, String>> pipelineDeployments = new LinkedList<>();

    @Override
    public void start(Future<Void> started) {
        readConfiguration()
                .map(this::prepareConfiguration)
                .flatMap(this::loadClasses)
                .flatMapCompletable(this::startPipelines)
                .subscribe(CompletableHelper.toObserver(started));

    }

    /**
     * Prepares the pipeline name by extracting it from configuratino
     *
     * @param json the global pipelines configuration
     * @return the configuration with each pipeline named. according to its entry name from configuration
     */
    private JsonObject prepareConfiguration(JsonObject json) {
        json.fieldNames().forEach(name -> json.getJsonObject(name).put("name", name));
        return json;
    }

    private Completable startPipelines(JsonObject config) {
        return fromIterable(config.fieldNames())
                .map(config::getJsonObject)
                .filter(Objects::nonNull)
                .map(this::startPipeline)
                .reduceWith(this::getPipelineDeployments, (list, deployment) -> {
                    deployment.subscribe((Consumer<Pair<String, String>>) list::add);
                    return list;
                }).toCompletable();

    }

    /**
     * Starts a pipeline with provided config.
     * <p>
     * If {@link PipelineVerticle} deployment fails, then the error is kept internally but
     * <b>DOES NOT STOP</b> the global deployment of the other pipelines.
     *
     * @param config the config to inject into the {@link PipelineVerticle}
     * @return a single containing a pair with the name of the pipeline and its deployment ID if any.
     */
    private Single<Pair<String, String>> startPipeline(JsonObject config) {
        return vertx.rxDeployVerticle(PIPELINE_VERTICLE, new DeploymentOptions().setConfig(config))
                .map(id -> Pair.of(config.getString("name"), id))
                .doOnSuccess(dep -> LOG.info("Deployed pipeline [{}]: {}", dep.getLeft(), dep.getRight()))
                .onErrorResumeNext((throwable) -> Single.just(Pair.of(config.getString("name"), DEPLOYMENT_ERROR)));
    }


    /**
     * Loads the configuration from a file to prepare all the pipelines.
     *
     * @return the loaded configuration as a {@link Single}
     */
    private Single<JsonObject> readConfiguration() {
        ConfigStoreOptions store = new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject()
                        .put("path", "config.yml")
                );

        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
                        .addStore(store)
//                .setIncludeDefaultStores(true)
        );

        return retriever.rxGetConfig();
    }

    /**
     * Get the list of pipeline deployments holded internally.
     *
     * @return the list of pipeline deployments
     */
    public LinkedList<Pair<String, String>> getPipelineDeployments() {
        return pipelineDeployments;
    }

    /**
     * Load classes asynchronously as it is a blocking operation.
     *
     * @param config the config
     * @return a single with the config
     */
    private Single<JsonObject> loadClasses(JsonObject config) {
        return vertx.rxExecuteBlocking(future -> {
            ClasspathHelpers.getScan();
            future.complete(config);
        });
    }

    //    private Single<OAuthCredentialsResponse> prepareConfiguration(JsonObject options) {
//        JsonObject sinkOpts = options
//            .getJsonObject("twitter-auth")
//            .getJsonObject("pump");
//
//        return vertx.rxExecuteBlocking(t -> {
//            OAuthCredentialsResponse params;
//            try {
//                params = new TwitterAuthenticator(System.out, sinkOpts.getString("consumerKey"), sinkOpts.getString("consumerSecret")).exchange();
//                LOG.info("token[{}], tokenSecret[{}]", params.token, params.tokenSecret);
//                t.complete(params);
//            } catch (TwitterAuthenticationException e) {
//                LOG.error("An error occured with twitter", e);
//                t.fail(e);
//            }
//        });
//
//    }
}
