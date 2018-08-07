package fr.myprysm.pipeline.elasticsearch.kotlin.sink

import fr.myprysm.pipeline.elasticsearch.sink.ElasticsearchSinkOptions
import fr.myprysm.pipeline.elasticsearch.sink.ElasticsearchSinkOptions.Strategy

fun ElasticsearchSinkOptions(
        bulk: Boolean? = null,
        bulkSize: Int? = null,
        field: String? = null,
        generateId: Strategy? = null,
        hosts: io.vertx.core.json.JsonArray? = null,
        indexName: String? = null,
        indexType: String? = null,
        name: String? = null,
        type: String? = null): ElasticsearchSinkOptions = fr.myprysm.pipeline.elasticsearch.sink.ElasticsearchSinkOptions().apply {

    if (bulk != null) {
        this.setBulk(bulk)
    }
    if (bulkSize != null) {
        this.setBulkSize(bulkSize)
    }
    if (field != null) {
        this.setField(field)
    }
    if (generateId != null) {
        this.setGenerateId(generateId)
    }
    if (hosts != null) {
        this.setHosts(hosts)
    }
    if (indexName != null) {
        this.setIndexName(indexName)
    }
    if (indexType != null) {
        this.setIndexType(indexType)
    }
    if (name != null) {
        this.setName(name)
    }
    if (type != null) {
        this.setType(type)
    }
}

