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
  ForkProcessorOptions_=ForkProcessorOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.processor.ForkProcessorOptions */
shared class ForkProcessorOptions(
  Integer? instances = null,
  String? name = null,
  " Publication addresses.\n <p>\n Those publications are broadcasted to each registered consumer.\n"
  shared {String*}? publish = null,
  " Publication addresses.\n <p>\n Those publications are casted to the first registered consumer that get the message.\n"
  shared {String*}? send = null,
  String? type = null) extends ProcessorOptions(
  instances,
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists publish) {
      json.put("publish", JsonArray(publish));
    }
    if (exists send) {
      json.put("send", JsonArray(send));
    }
    return json;
  }
}

shared object forkProcessorOptions {

  shared ForkProcessorOptions fromJson(JsonObject json) {
    Integer? instances = json.getIntegerOrNull("instances");
    String? name = json.getStringOrNull("name");
    {String*}? publish = json.getArrayOrNull("publish")?.strings;
    {String*}? send = json.getArrayOrNull("send")?.strings;
    String? type = json.getStringOrNull("type");
    return ForkProcessorOptions {
      instances = instances;
      name = name;
      publish = publish;
      send = send;
      type = type;
    };
  }

  shared object toCeylon extends Converter<ForkProcessorOptions_, ForkProcessorOptions>() {
    shared actual ForkProcessorOptions convert(ForkProcessorOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<ForkProcessorOptions, ForkProcessorOptions_>() {
    shared actual ForkProcessorOptions_ convert(ForkProcessorOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = ForkProcessorOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(ForkProcessorOptions obj) => obj.toJson();
}
