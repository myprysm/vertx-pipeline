import fr.myprysm.pipeline.ceylon.pipeline.core.sink {
  SinkOptions
}
import fr.myprysm.pipeline.elasticsearch.sink {
  ElasticsearchSinkOptions_=ElasticsearchSinkOptions
}
import ceylon.json {
  JsonObject=Object,
  JsonArray=Array,
  parse
}
import io.vertx.lang.ceylon {
  BaseDataObject,
  Converter,
  ToJava
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.elasticsearch.sink.ElasticsearchSinkOptions */
shared class ElasticsearchSinkOptions(
  " Indicates whether bulk is enabled.\n"
  shared Boolean? bulk = null,
  " The bulk size for bulk index operations.\n <p>\n Must be a positive integer\n <p>\n This option is ignored when <code>bulk</code> is <code>false</code>.\n"
  shared Integer? bulkSize = null,
  " The field to extract as ID in case <code>generateId</code> is set to <code>field</code>.\n"
  shared String? field = null,
  " The ID generation strategy.\n\n <code>none</code> is the default. It will let elasticsearch generate an ID automatically.\n\n <code>uuid</code> will generate a new uuid for every document.\n\n <code>field</code> used in combination with the <code>field</code> option\n will extract the ID as a string from the provided path in the tree.\n"
  shared String? generateId = null,
  " The remote elasticsearch cluster hosts\n <p>\n Must be a list of objects as follows.\n <p>\n YAML:\n <pre>\n - hostname: your.host\n   port: 9300\n - hostname: 127.0.0.1\n   port: 9300\n </pre>\n <p>\n JSON:\n <pre>\n [\n     {\n         \"hostname\": \"your.host\",\n         \"port\": 9300\n     },\n     {\n         \"hostname\": \"127.0.0.1\",\n         \"port\": 9300\n     }\n ]\n </pre>\n"
  shared JsonArray? hosts = null,
  " The index name to store incoming events.\n"
  shared String? indexName = null,
  " The index type to store incoming events\n"
  shared String? indexType = null,
  String? name = null,
  String? type = null) extends SinkOptions(
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists bulk) {
      json.put("bulk", bulk);
    }
    if (exists bulkSize) {
      json.put("bulkSize", bulkSize);
    }
    if (exists field) {
      json.put("field", field);
    }
    if (exists generateId) {
      json.put("generateId", generateId);
    }
    if (exists hosts) {
      json.put("hosts", hosts);
    }
    if (exists indexName) {
      json.put("indexName", indexName);
    }
    if (exists indexType) {
      json.put("indexType", indexType);
    }
    return json;
  }
}

shared object elasticsearchSinkOptions {

  shared ElasticsearchSinkOptions fromJson(JsonObject json) {
    Boolean? bulk = json.getBooleanOrNull("bulk");
    Integer? bulkSize = json.getIntegerOrNull("bulkSize");
    String? field = json.getStringOrNull("field");
    String? generateId = json.getStringOrNull("generateId");
    JsonArray? hosts = json.getArrayOrNull("hosts");
    String? indexName = json.getStringOrNull("indexName");
    String? indexType = json.getStringOrNull("indexType");
    String? name = json.getStringOrNull("name");
    String? type = json.getStringOrNull("type");
    return ElasticsearchSinkOptions {
      bulk = bulk;
      bulkSize = bulkSize;
      field = field;
      generateId = generateId;
      hosts = hosts;
      indexName = indexName;
      indexType = indexType;
      name = name;
      type = type;
    };
  }

  shared object toCeylon extends Converter<ElasticsearchSinkOptions_, ElasticsearchSinkOptions>() {
    shared actual ElasticsearchSinkOptions convert(ElasticsearchSinkOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<ElasticsearchSinkOptions, ElasticsearchSinkOptions_>() {
    shared actual ElasticsearchSinkOptions_ convert(ElasticsearchSinkOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = ElasticsearchSinkOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(ElasticsearchSinkOptions obj) => obj.toJson();
}
