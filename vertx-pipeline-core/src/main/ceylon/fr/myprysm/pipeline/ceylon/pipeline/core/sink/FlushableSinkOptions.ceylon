import fr.myprysm.pipeline.ceylon.pipeline.core.sink {
  SinkOptions
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
import fr.myprysm.pipeline.sink {
  FlushableSinkOptions_=FlushableSinkOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.sink.FlushableSinkOptions */
shared class FlushableSinkOptions(
  " The batch size of the `fr.myprysm.pipeline.sink.FileSink`.\n <p>\n It must be a positive `java.lang.Integer`.\n <p>\n It defaults to <code>10</code>\n"
  shared Integer? batchSize = null,
  String? name = null,
  String? type = null) extends SinkOptions(
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists batchSize) {
      json.put("batchSize", batchSize);
    }
    return json;
  }
}

shared object flushableSinkOptions {

  shared FlushableSinkOptions fromJson(JsonObject json) {
    Integer? batchSize = json.getIntegerOrNull("batchSize");
    String? name = json.getStringOrNull("name");
    String? type = json.getStringOrNull("type");
    return FlushableSinkOptions {
      batchSize = batchSize;
      name = name;
      type = type;
    };
  }

  shared object toCeylon extends Converter<FlushableSinkOptions_, FlushableSinkOptions>() {
    shared actual FlushableSinkOptions convert(FlushableSinkOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<FlushableSinkOptions, FlushableSinkOptions_>() {
    shared actual FlushableSinkOptions_ convert(FlushableSinkOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = FlushableSinkOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(FlushableSinkOptions obj) => obj.toJson();
}
