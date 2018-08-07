import fr.myprysm.pipeline.pump {
  PumpOptions_=PumpOptions
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
/* Generated from fr.myprysm.pipeline.pump.PumpOptions */
shared class PumpOptions(
  " The name of the pump.\n <p>\n This is automatically populated when the pipeline configuration is a list.\n <p>\n You still can name your pump for any purpose by using a map instead of a list\n when you describe your pipeline.\n"
  shared String? name = null,
  " The type of the pump.\n <p>\n This is the fully qualified name of the <code>class</code> that acts as pump.\n"
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

shared object pumpOptions {

  shared PumpOptions fromJson(JsonObject json) {
    String? name = json.getStringOrNull("name");
    String? type = json.getStringOrNull("type");
    return PumpOptions {
      name = name;
      type = type;
    };
  }

  shared object toCeylon extends Converter<PumpOptions_, PumpOptions>() {
    shared actual PumpOptions convert(PumpOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<PumpOptions, PumpOptions_>() {
    shared actual PumpOptions_ convert(PumpOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = PumpOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(PumpOptions obj) => obj.toJson();
}
