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
import fr.myprysm.pipeline.processor {
  ProcessorOptions_=ProcessorOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.processor.ProcessorOptions */
shared class ProcessorOptions(
  " The number of instances to deploy.\n <p>\n Must be a positive `java.lang.Integer`.\n"
  shared Integer? instances = null,
  " The name of the processor.\n <p>\n This is automatically populated when the pipeline configuration is a list.\n <p>\n You still can name your processor for any purpose by using a map instead of a list\n when you describe your pipeline.\n"
  shared String? name = null,
  " The type of the processor.\n <p>\n This is the fully qualified name of the <code>class</code> that acts as processor.\n"
  shared String? type = null) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = JsonObject();
    if (exists instances) {
      json.put("instances", instances);
    }
    if (exists name) {
      json.put("name", name);
    }
    if (exists type) {
      json.put("type", type);
    }
    return json;
  }
}

shared object processorOptions {

  shared ProcessorOptions fromJson(JsonObject json) {
    Integer? instances = json.getIntegerOrNull("instances");
    String? name = json.getStringOrNull("name");
    String? type = json.getStringOrNull("type");
    return ProcessorOptions {
      instances = instances;
      name = name;
      type = type;
    };
  }

  shared object toCeylon extends Converter<ProcessorOptions_, ProcessorOptions>() {
    shared actual ProcessorOptions convert(ProcessorOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<ProcessorOptions, ProcessorOptions_>() {
    shared actual ProcessorOptions_ convert(ProcessorOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = ProcessorOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(ProcessorOptions obj) => obj.toJson();
}
