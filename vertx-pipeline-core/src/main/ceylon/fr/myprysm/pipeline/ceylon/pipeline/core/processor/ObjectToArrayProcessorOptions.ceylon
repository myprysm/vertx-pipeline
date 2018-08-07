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
  ObjectToArrayProcessorOptions_=ObjectToArrayProcessorOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.processor.ObjectToArrayProcessorOptions */
shared class ObjectToArrayProcessorOptions(
  " The list of fields to transform as an array / list.\n <p>\n If one of the fields is not found in the input object, the field is created with an empty array.\n"
  shared JsonArray? fields = null,
  Integer? instances = null,
  String? name = null,
  String? type = null) extends ProcessorOptions(
  instances,
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists fields) {
      json.put("fields", fields);
    }
    return json;
  }
}

shared object objectToArrayProcessorOptions {

  shared ObjectToArrayProcessorOptions fromJson(JsonObject json) {
    JsonArray? fields = json.getArrayOrNull("fields");
    Integer? instances = json.getIntegerOrNull("instances");
    String? name = json.getStringOrNull("name");
    String? type = json.getStringOrNull("type");
    return ObjectToArrayProcessorOptions {
      fields = fields;
      instances = instances;
      name = name;
      type = type;
    };
  }

  shared object toCeylon extends Converter<ObjectToArrayProcessorOptions_, ObjectToArrayProcessorOptions>() {
    shared actual ObjectToArrayProcessorOptions convert(ObjectToArrayProcessorOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<ObjectToArrayProcessorOptions, ObjectToArrayProcessorOptions_>() {
    shared actual ObjectToArrayProcessorOptions_ convert(ObjectToArrayProcessorOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = ObjectToArrayProcessorOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(ObjectToArrayProcessorOptions obj) => obj.toJson();
}
