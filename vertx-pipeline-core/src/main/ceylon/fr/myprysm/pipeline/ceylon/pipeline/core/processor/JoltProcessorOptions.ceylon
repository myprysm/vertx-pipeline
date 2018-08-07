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
  JoltProcessorOptions_=JoltProcessorOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.processor.JoltProcessorOptions */
shared class JoltProcessorOptions(
  " The format of the JOLT specs file.\n <p>\n Either <code>json</code> or <code>yaml</code>\n"
  shared String? format = null,
  Integer? instances = null,
  String? name = null,
  " The path to a JOLT specs file.\n <p>\n The file can be either <code>JSON</code> or <code>YAML</code>.\n It must be a list of JOLT transforms. Documentation can be found on their <a href=\"http://bazaarvoice.github.io/jolt/\">website</a>.\n <p>\n <b>This option is exclusive with <code>specs</code> option. If a file is set and valid, file specs will overwrite <code>specs</code>.</b>\n"
  shared String? path = null,
  " The JOLT specs as a list of transforms.\n <p>\n Please check JOLT documentation on their <a href=\"http://bazaarvoice.github.io/jolt/\">website</a>.\n <p>\n <b>This option is exclusive with <code>path</code> option. If a file is set and valid, <code>specs</code> will be overwritten.</b>\n"
  shared JsonArray? specs = null,
  String? type = null) extends ProcessorOptions(
  instances,
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists format) {
      json.put("format", format);
    }
    if (exists path) {
      json.put("path", path);
    }
    if (exists specs) {
      json.put("specs", specs);
    }
    return json;
  }
}

shared object joltProcessorOptions {

  shared JoltProcessorOptions fromJson(JsonObject json) {
    String? format = json.getStringOrNull("format");
    Integer? instances = json.getIntegerOrNull("instances");
    String? name = json.getStringOrNull("name");
    String? path = json.getStringOrNull("path");
    JsonArray? specs = json.getArrayOrNull("specs");
    String? type = json.getStringOrNull("type");
    return JoltProcessorOptions {
      format = format;
      instances = instances;
      name = name;
      path = path;
      specs = specs;
      type = type;
    };
  }

  shared object toCeylon extends Converter<JoltProcessorOptions_, JoltProcessorOptions>() {
    shared actual JoltProcessorOptions convert(JoltProcessorOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<JoltProcessorOptions, JoltProcessorOptions_>() {
    shared actual JoltProcessorOptions_ convert(JoltProcessorOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = JoltProcessorOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(JoltProcessorOptions obj) => obj.toJson();
}
