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
  SinkOptions_=SinkOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.sink.SinkOptions */
shared class SinkOptions(
  " The name of the sink.\n <p>\n This is automatically populated when the pipeline configuration is a list.\n <p>\n You still can name your sink for any purpose by using a map instead of a list\n when you describe your pipeline.\n"
  shared String? name = null,
  " The type of the sink.\n <p>\n This is the fully qualified name of the <code>class</code> that acts as sink.\n"
  shared String? type = null) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = JsonObject();
    if (exists name) {
      json.put("name", name);
    }
    if (exists type) {
      json.put("type", type);
    }
    return json;
  }
}

shared object sinkOptions {

  shared SinkOptions fromJson(JsonObject json) {
    String? name = json.getStringOrNull("name");
    String? type = json.getStringOrNull("type");
    return SinkOptions {
      name = name;
      type = type;
    };
  }

  shared object toCeylon extends Converter<SinkOptions_, SinkOptions>() {
    shared actual SinkOptions convert(SinkOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<SinkOptions, SinkOptions_>() {
    shared actual SinkOptions_ convert(SinkOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = SinkOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(SinkOptions obj) => obj.toJson();
}
