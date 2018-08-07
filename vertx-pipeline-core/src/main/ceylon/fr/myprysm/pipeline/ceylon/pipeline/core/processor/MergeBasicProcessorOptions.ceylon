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
import fr.myprysm.pipeline.ceylon.pipeline.core.processor {
  ProcessorOptions
}
import fr.myprysm.pipeline.processor {
  MergeBasicProcessorOptions_=MergeBasicProcessorOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.processor.MergeBasicProcessorOptions */
shared class MergeBasicProcessorOptions(
  " The default capacity of the accumulated map, configured on startup.\n <p>\n"
  shared Integer? defaultCapacity = null,
  Integer? instances = null,
  String? name = null,
  " The list of operations to apply on the accumulated map.\n <p>\n Operations available:\n <ul>\n <li>sort: sorts the objects based on the value at path. Default is ASC</li>\n </ul>\n If the operation is not one of those allowed, it is ignored.\n"
  shared JsonObject? onFlush = null,
  " The list of operations to apply on the accumulating map.\n <p>\n Operations available:\n <ul>\n <li>objToKey: put the object in the map with the value at path as identifier</li>\n <li>mergeArrays: merges the arrays at given path on cached object</li>\n <li>sortArray: sorts the array at field in the corresponding order. Default is ASC</li>\n </ul>\n <p>\n objToKey is required.\n <p>\n If the operation is not one of those allowed, it is ignored.\n"
  shared JsonObject? operations = null,
  String? type = null) extends ProcessorOptions(
  instances,
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists defaultCapacity) {
      json.put("defaultCapacity", defaultCapacity);
    }
    if (exists onFlush) {
      json.put("onFlush", onFlush);
    }
    if (exists operations) {
      json.put("operations", operations);
    }
    return json;
  }
}

shared object mergeBasicProcessorOptions {

  shared MergeBasicProcessorOptions fromJson(JsonObject json) {
    Integer? defaultCapacity = json.getIntegerOrNull("defaultCapacity");
    Integer? instances = json.getIntegerOrNull("instances");
    String? name = json.getStringOrNull("name");
    JsonObject? onFlush = json.getObjectOrNull("onFlush");
    JsonObject? operations = json.getObjectOrNull("operations");
    String? type = json.getStringOrNull("type");
    return MergeBasicProcessorOptions {
      defaultCapacity = defaultCapacity;
      instances = instances;
      name = name;
      onFlush = onFlush;
      operations = operations;
      type = type;
    };
  }

  shared object toCeylon extends Converter<MergeBasicProcessorOptions_, MergeBasicProcessorOptions>() {
    shared actual MergeBasicProcessorOptions convert(MergeBasicProcessorOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<MergeBasicProcessorOptions, MergeBasicProcessorOptions_>() {
    shared actual MergeBasicProcessorOptions_ convert(MergeBasicProcessorOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = MergeBasicProcessorOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(MergeBasicProcessorOptions obj) => obj.toJson();
}
