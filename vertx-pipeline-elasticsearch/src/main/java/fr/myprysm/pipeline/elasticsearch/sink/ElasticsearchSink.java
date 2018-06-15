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

import com.fasterxml.jackson.core.type.TypeReference;
import fr.myprysm.pipeline.sink.BaseJsonSink;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static fr.myprysm.pipeline.util.JsonHelpers.extractString;
import static fr.myprysm.pipeline.validation.JsonValidation.ENV_PREFIX;

public class ElasticsearchSink extends BaseJsonSink<ElasticsearchSinkOptions> implements FlowableOnSubscribe<JsonObject> {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchSink.class);

    private RestHighLevelClient esClient;

    private Integer bulkSize;
    private JsonArray hosts;
    private String type;
    private String index;
    private FlowableEmitter<JsonObject> emitter;
    private Flowable<JsonObject> flowable;
    private Flowable<List<JsonObject>> bulkFlowable;
    private Disposable sub;
    private ElasticsearchSinkOptions.Strategy strategy;
    private String field;

    @Override
    public void drain(JsonObject item) {
        emitter.onNext(item);
    }

    private void index(JsonObject event) {
        esClient.indexAsync(prepareRequest(event).source(toMap(event)), new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {

            }

            @Override
            public void onFailure(Exception e) {
                error("Error while indexing document.", e);
            }
        });
    }

    /**
     * Prepares the request according to the defined strategy.
     * <p>
     * Try to extract the id from the configured field. When not able, just acts as <code>none</code>
     *
     * @param event the event
     * @return the request
     */
    private IndexRequest prepareRequest(JsonObject event) {
        String id = null;
        if (strategy == ElasticsearchSinkOptions.Strategy.uuid) {
            id = UUID.randomUUID().toString();
        } else if (strategy == ElasticsearchSinkOptions.Strategy.field) {
            id = extractString(event, field).orElse(null);
            if (id == null) {
                info("Id could not be extracted [{}]", field, event);
            }
        }

        return id != null ? new IndexRequest(index, type, id) : new IndexRequest(index, type);
    }

    private void bulkIndex(List<JsonObject> events) {
        esClient.bulkAsync(prepareBulk(events), new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkItemResponses) {

            }

            @Override
            public void onFailure(Exception e) {
                error("Error while bulk indexing document.", e);
            }
        });
    }

    private BulkRequest prepareBulk(List<JsonObject> events) {
        BulkRequest request = new BulkRequest();

        for (JsonObject event : events) {
            IndexRequest indexRequest = prepareRequest(event);
            Map<String, Object> stringObjectMap = toMap(event);
            request.add(indexRequest.source(stringObjectMap));
        }
        return request;
    }

    @Override
    protected Completable startVerticle() {
        // workaround for problem between ES netty and vertx (both wanting to set the same value)
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        return Completable.fromAction(() -> {
            HttpHost[] httpHosts = hosts.stream()
                    .map(JsonObject.class::cast)
                    .map(this::mapHost)
                    .toArray(HttpHost[]::new);


            info("connecting to {}...", Arrays.toString(httpHosts));
            RestClientBuilder builder = RestClient.builder(httpHosts);
            esClient = new RestHighLevelClient(builder);
            if (esClient.ping()) {
                info("Connected to elasticsearch.");
                if (bulkFlowable != null) {
                    sub = bulkFlowable.subscribe(ElasticsearchSink.this::bulkIndex);
                } else {
                    sub = flowable.subscribe(ElasticsearchSink.this::index);
                }
            } else {
                error("unable to connect to {}...", Arrays.toString(httpHosts));
                throw new UnknownHostException("Unable to connect to elasticsearch...");
            }
        });
    }

    /**
     * Overrides the ES hosts with environment when provided.
     *
     * @param config the sink raw configuration
     * @return the configuration updated with values from env.
     */
    @Override
    protected JsonObject preConfiguration(JsonObject config) {
        JsonObject copy = super.preConfiguration(config).copy();

        if (copy.getJsonArray("hosts") != null) {
            List<JsonObject> hosts = copy.getJsonArray("hosts")
                    .stream()
                    .map(JsonObject.class::cast)
                    .map(this::extractEnvHost)
                    .collect(Collectors.toList());

            copy.put("hosts", hosts);
        }
        return copy;
    }

    private JsonObject extractEnvHost(JsonObject host) {
        JsonObject copy = host.copy();
        if (copy.getValue("hostname") instanceof String) {
            if (copy.getString("hostname").startsWith(ENV_PREFIX)) {
                String hostname = getEnvAsString(copy.getString("hostname"));
                copy.put("hostname", hostname);
                info("added hostname {} from env", hostname);
            }
        }

        if (copy.getValue("port") instanceof String) {
            Integer port = getEnvAsInt(copy.getString("port"));
            copy.put("port", port != null ? port : 9200);
            info("added port {} from env", port);
        }

        return copy;
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
        strategy = config.getGenerateId();
        if (strategy == ElasticsearchSinkOptions.Strategy.field) {
            field = config.getField();
        }
        index = config.getIndexName();
        type = config.getIndexType();
        Boolean bulk = config.getBulk();
        bulkSize = config.getBulkSize();
        hosts = config.getHosts();
        flowable = Flowable.create(ElasticsearchSink.this, BackpressureStrategy.BUFFER);

        if (bulk) {
            bulkFlowable = flowable.buffer(bulkSize);
        }

        return Completable.complete();
    }

    private HttpHost mapHost(JsonObject host) {
        HttpHost httpHost;
        if (host.getValue("port") instanceof Number) {
            if (host.getValue("protocol") instanceof String) {
                httpHost = new HttpHost(host.getString("hostname"), host.getInteger("port"), host.getString("protocol"));
            } else {
                httpHost = new HttpHost(host.getString("hostname"), host.getInteger("port"));
            }
        } else {
            httpHost = HttpHost.create(host.getString("hostname"));
        }

        return httpHost;
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return ElasticsearchSinkOptionsValidation.validate(config);
    }


    @Override
    public void subscribe(FlowableEmitter<JsonObject> emitter) throws Exception {
        this.emitter = emitter;
    }

    @Override
    protected Logger delegate() {
        return LOG;
    }

    private Map<String, Object> toMap(JsonObject json) {
        return Json.mapper.convertValue(json.copy(), new TypeReference<Map<String, Object>>() {
        });
    }
}
