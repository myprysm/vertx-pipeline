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
  LogProcessorOptions_=LogProcessorOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.processor.LogProcessorOptions */
" Log Processor options.\n <p>\n They extend directly base [ProcessorOptions](../processor/ProcessorOptions.type.html) and provide\n the ability to set the acceptable level to log incoming messages.\n"
shared class LogProcessorOptions(
  Integer? instances = null,
  " The log  to write the incoming items.\n <p>\n Defaults to <code>DEBUG</code>.\n <p>\n One of:\n <ul>\n <li><code>TRACE</code></li>\n <li><code>DEBUG</code></li>\n <li><code>INFO</code></li>\n <li><code>WARN</code></li>\n <li><code>ERROR</code></li>\n </ul>\n"
  shared String? level = null,
  String? name = null,
  String? type = null) extends ProcessorOptions(
  instances,
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists level) {
      json.put("level", level);
    }
    return json;
  }
}

shared object logProcessorOptions {

  shared LogProcessorOptions fromJson(JsonObject json) {
    Integer? instances = json.getIntegerOrNull("instances");
    String? level = json.getStringOrNull("level");
    String? name = json.getStringOrNull("name");
    String? type = json.getStringOrNull("type");
    return LogProcessorOptions {
      instances = instances;
      level = level;
      name = name;
      type = type;
    };
  }

  shared object toCeylon extends Converter<LogProcessorOptions_, LogProcessorOptions>() {
    shared actual LogProcessorOptions convert(LogProcessorOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<LogProcessorOptions, LogProcessorOptions_>() {
    shared actual LogProcessorOptions_ convert(LogProcessorOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = LogProcessorOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(LogProcessorOptions obj) => obj.toJson();
}
