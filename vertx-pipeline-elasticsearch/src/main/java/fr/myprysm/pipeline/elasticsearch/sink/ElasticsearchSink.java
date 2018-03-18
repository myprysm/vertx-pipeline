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

package fr.myprysm.pipeline.elasticsearch.sink;

import com.hubrick.vertx.elasticsearch.impl.DefaultElasticSearchAdminService;
import com.hubrick.vertx.elasticsearch.impl.DefaultElasticSearchService;
import com.hubrick.vertx.elasticsearch.impl.DefaultTransportClientFactory;
import com.hubrick.vertx.elasticsearch.impl.JsonElasticSearchConfigurator;
import com.hubrick.vertx.elasticsearch.model.BulkIndexOptions;
import com.hubrick.vertx.elasticsearch.model.BulkOptions;
import fr.myprysm.pipeline.sink.BaseJsonSink;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static java.util.stream.Collectors.toList;

public class ElasticsearchSink extends BaseJsonSink<ElasticsearchSinkOptions> implements FlowableOnSubscribe<JsonObject> {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchSink.class);
    private DefaultElasticSearchService es;
    private DefaultElasticSearchAdminService esAdmin;

    private Integer bulkSize;
    private String cluster;
    private JsonArray hosts;
    private String type;
    private String index;
    private FlowableEmitter<JsonObject> emitter;
    private Flowable<JsonObject> flowable;
    private Flowable<List<JsonObject>> bulkFlowable;
    private Disposable sub;

    @Override
    public void drain(JsonObject item) {
        emitter.onNext(item);
    }

    private void index(JsonObject event) {
        es.index(index, type, event, this::handleEsResponse);
    }

    private void bulkIndex(List<JsonObject> events) {
        es.bulkIndex(prepareBulk(events), new BulkOptions(), this::handleEsResponse);
    }

    private List<BulkIndexOptions> prepareBulk(List<JsonObject> events) {
        return events.stream().map(new BulkIndexOptions().setIndex(index).setType(type)::setSource).collect(toList());
    }

    @Override
    protected Completable startVerticle() {
        // workaround for problem between ES netty and vertx (both wanting to set the same value)
        System.setProperty("es.set.netty.runtime.available.processors", "false");

        return Completable.create((emitter) -> {
            es = new DefaultElasticSearchService(new DefaultTransportClientFactory(), new JsonElasticSearchConfigurator(obj()
                    .put("cluster_name", cluster)
                    .put("transportAddresses", hosts)
            ));
            es.start();
            esAdmin = new DefaultElasticSearchAdminService(es);
            esAdmin.getAdmin().cluster().health(new ClusterHealthRequest(), new ActionListener<ClusterHealthResponse>() {
                @Override
                public void onResponse(ClusterHealthResponse clusterHealthResponse) {
                    info("Connected to elasticsearch: {}", clusterHealthResponse);
                    if (bulkFlowable != null) {
                        sub = bulkFlowable.subscribe(ElasticsearchSink.this::bulkIndex);
                    } else {
                        sub = flowable.subscribe(ElasticsearchSink.this::index);
                    }

                    emitter.onComplete();
                }

                @Override
                public void onFailure(Exception e) {
                    emitter.onError(e);
                }
            });
        });
    }

    @Override
    public Completable shutdown() {
        emitter.onComplete();
        sub.dispose();
        return Completable.complete();
    }

    @Override
    public ElasticsearchSinkOptions readConfiguration(JsonObject config) {
        return new ElasticsearchSinkOptions(config);
    }

    @Override
    public Completable configure(ElasticsearchSinkOptions config) {
        index = config.getIndexName();
        type = config.getIndexType();
        Boolean bulk = config.getBulk();
        bulkSize = config.getBulkSize();
        cluster = config.getCluster();
        hosts = config.getHosts();
        flowable = Flowable.create(ElasticsearchSink.this, BackpressureStrategy.BUFFER);

        if (bulk) {
            bulkFlowable = flowable.buffer(bulkSize);
        }

        return Completable.complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return ElasticsearchSinkOptionsValidation.validate(config);
    }

    private void handleEsResponse(AsyncResult op) {
        if (op.failed()) {
            error("an error occured while indexing item: ", op.cause());
        }
    }

    @Override
    public void subscribe(FlowableEmitter<JsonObject> emitter) throws Exception {
        this.emitter = emitter;
    }

    @Override
    protected Logger delegate() {
        return LOG;
    }
}
