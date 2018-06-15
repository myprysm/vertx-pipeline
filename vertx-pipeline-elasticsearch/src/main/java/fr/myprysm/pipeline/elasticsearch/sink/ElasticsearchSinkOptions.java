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

import fr.myprysm.pipeline.sink.SinkOptions;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static fr.myprysm.pipeline.util.JsonHelpers.arr;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;

@DataObject(generateConverter = true)
public class ElasticsearchSinkOptions extends SinkOptions {
    private static final JsonArray DEFAULT_HOSTS = arr().add(obj().put("hostname", "localhost").put("port", 9200));
    private static final Boolean DEFAULT_BULK = false;
    private static final Integer DEFAULT_BULK_SIZE = 100;
    private static final Strategy DEFAULT_STRATEGY = Strategy.none;
    private Strategy generateId = DEFAULT_STRATEGY;
    private String field;
    public ElasticsearchSinkOptions(ElasticsearchSinkOptions other) {
        super(other);
        generateId = other.generateId;
        field = other.field;
        indexName = other.indexName;
        indexType = other.indexType;
        bulk = other.bulk;
        bulkSize = other.bulkSize;
        hosts = other.hosts;
    }

    private String indexName;
    private String indexType;

    private Boolean bulk = DEFAULT_BULK;
    private Integer bulkSize = DEFAULT_BULK_SIZE;
    private JsonArray hosts = DEFAULT_HOSTS;

    public ElasticsearchSinkOptions() {
        super();
    }

    /**
     * The index name
     *
     * @return the index name
     */
    public Strategy getGenerateId() {
        return generateId;
    }

    public ElasticsearchSinkOptions(SinkOptions other) {
        super(other);
    }

    public ElasticsearchSinkOptions(JsonObject json) {
        super(json);
        ElasticsearchSinkOptionsConverter.fromJson(json, this);
    }

    /**
     * The ID generation strategy.
     *
     * <code>none</code> is the default. It will let elasticsearch generate an ID automatically.
     *
     * <code>uuid</code> will generate a new uuid for every document.
     *
     * <code>field</code> used in combination with the <code>field</code> option
     * will extract the ID as a string from the provided path in the tree.
     *
     * @param strategy the strategy
     * @return this
     */
    public ElasticsearchSinkOptions setGenerateId(Strategy strategy) {
        generateId = strategy;
        return this;
    }

    /**
     * The field
     *
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * The field to extract as ID in case <code>generateId</code> is set to <code>field</code>.
     *
     * @param field the field to extract
     * @return this
     */
    public ElasticsearchSinkOptions setField(String field) {
        this.field = field;
        return this;
    }

    public enum Strategy {none, uuid, field}

    /**
     * The index name
     *
     * @return the index name
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * The index name to store incoming events.
     *
     * @param indexName the index name
     * @return this
     */
    public ElasticsearchSinkOptions setIndexName(String indexName) {
        this.indexName = indexName;
        return this;
    }

    /**
     * The index type
     *
     * @return the index type
     */
    public String getIndexType() {
        return indexType;
    }

    /**
     * The index type to store incoming events
     *
     * @param indexType the index type
     * @return this
     */
    public ElasticsearchSinkOptions setIndexType(String indexType) {
        this.indexType = indexType;
        return this;
    }

    /**
     * Indicates whether bulk is enabled
     *
     * @return <code>true</code> when bulk is enabled
     */
    public Boolean getBulk() {
        return bulk;
    }

    /**
     * Indicates whether bulk is enabled.
     *
     * @param bulk <code>true</code> when bulk is enabled
     * @return this
     */
    public ElasticsearchSinkOptions setBulk(Boolean bulk) {
        this.bulk = bulk;
        return this;
    }

    /**
     * The bulk size
     *
     * @return the bulk size
     */
    public Integer getBulkSize() {
        return bulkSize;
    }

    /**
     * The bulk size for bulk index operations.
     * <p>
     * Must be a positive integer
     * <p>
     * This option is ignored when <code>bulk</code> is <code>false</code>.
     *
     * @param bulkSize the bulk size
     * @return this
     */
    public ElasticsearchSinkOptions setBulkSize(Integer bulkSize) {
        this.bulkSize = bulkSize;
        return this;
    }

    /**
     * The hosts
     *
     * @return the hosts
     */
    public JsonArray getHosts() {
        return hosts;
    }

    /**
     * The remote elasticsearch cluster hosts
     * <p>
     * Must be a list of objects as follows.
     * <p>
     * YAML:
     * <pre>
     * - hostname: your.host
     *   port: 9300
     * - hostname: 127.0.0.1
     *   port: 9300
     * </pre>
     * <p>
     * JSON:
     * <pre>
     * [
     *     {
     *         "hostname": "your.host",
     *         "port": 9300
     *     },
     *     {
     *         "hostname": "127.0.0.1",
     *         "port": 9300
     *     }
     * ]
     * </pre>
     *
     * @param hosts the hosts
     * @return this
     */
    public ElasticsearchSinkOptions setHosts(JsonArray hosts) {
        this.hosts = hosts;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public ElasticsearchSinkOptions setName(String name) {
        return (ElasticsearchSinkOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public ElasticsearchSinkOptions setType(String type) {
        return (ElasticsearchSinkOptions) super.setType(type);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        ElasticsearchSinkOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElasticsearchSinkOptions)) return false;
        if (!super.equals(o)) return false;
        ElasticsearchSinkOptions that = (ElasticsearchSinkOptions) o;
        return Objects.equals(indexName, that.indexName) &&
                Objects.equals(indexType, that.indexType) &&
                Objects.equals(bulk, that.bulk) &&
                Objects.equals(bulkSize, that.bulkSize) &&
                Objects.equals(hosts, that.hosts);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), indexName, indexType, bulk, bulkSize, hosts);
    }


    @Override
    public String toString() {
        return "ElasticsearchSinkOptions{" +
                "indexName='" + indexName + '\'' +
                ", indexType='" + indexType + '\'' +
                ", bulk=" + bulk +
                ", bulkSize=" + bulkSize +
                ", hosts=" + hosts +
                "} " + super.toString();
    }
}
