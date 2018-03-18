# Vert.x Pipeline Elasticsearch

Components for [Elasticsearch](https://www.elastic.co/products/elasticsearch).

Simple wrapper around [vertx-elasticsearch-service](https://github.com/hubrick/vertx-elasticsearch-service)
to provide `pumps`, `processors` and a `sink`.

Please note that this connector is **experimental**.

## Usage

Add it as a maven dependency to your project:
```
<dependency>
    <groupId>fr.myprysm</groupId>
    <artifactId>vertx-pipeline-elasticsearch</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

To package your application with vertx-pipeline and the extension as a `fatjar`,
please refer to instructions on the [homepage](https://github.com/myprysm/vertx-pipeline) of the project.

## Components

At the moment each component will hold its own elasticsearch service for its operations.

None of them will prepare mapping for you so ensure that you made it... 
Anyway all those components should easily allow to migrate some indexes to other locations as quick as it can ;)

### ElasticsearchSink

This sink drains the incoming events into an elasticsearch index.
Provides the ability to bulk index documents for high throughput.
Please check [options][es-sink-options] for more information


[es-sink-options]:https://github.com/myprysm/vertx-pipeline/blob/develop/vertx-pipeline-elasticsearch/src/main/asciidoc/dataobjects.adoc#elasticsearchsinkoptions