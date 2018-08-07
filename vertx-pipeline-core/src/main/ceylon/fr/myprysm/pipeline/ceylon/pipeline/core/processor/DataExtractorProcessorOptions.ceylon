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
  DataExtractorProcessorOptions_=DataExtractorProcessorOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.processor.DataExtractorProcessorOptions */
shared class DataExtractorProcessorOptions(
  " The list of fields to extract from input json.\n <p>\n If one of the fields is not found in the input object, the field is ignored.\n <p>\n As key, the path to extract a value from input event.\n As value, the path to put this value to the output event.\n <p>\n The complete event can be referenced with the keywork <code>$event</code>\n"
  shared JsonObject? extract = null,
  Integer? instances = null,
  String? name = null,
  String? type = null) extends ProcessorOptions(
  instances,
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists extract) {
      json.put("extract", extract);
    }
    return json;
  }
}

shared object dataExtractorProcessorOptions {

  shared DataExtractorProcessorOptions fromJson(JsonObject json) {
    JsonObject? extract = json.getObjectOrNull("extract");
    Integer? instances = json.getIntegerOrNull("instances");
    String? name = json.getStringOrNull("name");
    String? type = json.getStringOrNull("type");
    return DataExtractorProcessorOptions {
      extract = extract;
      instances = instances;
      name = name;
      type = type;
    };
  }

  shared object toCeylon extends Converter<DataExtractorProcessorOptions_, DataExtractorProcessorOptions>() {
    shared actual DataExtractorProcessorOptions convert(DataExtractorProcessorOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<DataExtractorProcessorOptions, DataExtractorProcessorOptions_>() {
    shared actual DataExtractorProcessorOptions_ convert(DataExtractorProcessorOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = DataExtractorProcessorOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(DataExtractorProcessorOptions obj) => obj.toJson();
}
