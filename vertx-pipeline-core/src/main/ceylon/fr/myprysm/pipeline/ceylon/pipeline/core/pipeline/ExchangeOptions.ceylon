import fr.myprysm.pipeline.pipeline {
  ExchangeOptions_=ExchangeOptions
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
/* Generated from fr.myprysm.pipeline.pipeline.ExchangeOptions */
shared class ExchangeOptions(
  " The control channel to emit/receive signals.\n <p>\n This is automatically configured when the pipeline is built.\n <b>The channel cannot be configured</b>\n"
  shared String? controlChannel = null,
  " The address the deployed object will receive items from.\n <p>\n This is automatically configured when the pipeline is built.\n <b>The address cannot be configured</b>\n"
  shared String? from = null,
  " The addresses the deployed object will send results to.\n <p>\n This is automatically configured when the pipeline is built.\n <b>The address cannot be configured</b>\n"
  shared {String*}? to = null) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = JsonObject();
    if (exists controlChannel) {
      json.put("controlChannel", controlChannel);
    }
    if (exists from) {
      json.put("from", from);
    }
    if (exists to) {
      json.put("to", JsonArray(to));
    }
    return json;
  }
}

shared object exchangeOptions {

  shared ExchangeOptions fromJson(JsonObject json) {
    String? controlChannel = json.getStringOrNull("controlChannel");
    String? from = json.getStringOrNull("from");
    {String*}? to = json.getArrayOrNull("to")?.strings;
    return ExchangeOptions {
      controlChannel = controlChannel;
      from = from;
      to = to;
    };
  }

  shared object toCeylon extends Converter<ExchangeOptions_, ExchangeOptions>() {
    shared actual ExchangeOptions convert(ExchangeOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<ExchangeOptions, ExchangeOptions_>() {
    shared actual ExchangeOptions_ convert(ExchangeOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = ExchangeOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(ExchangeOptions obj) => obj.toJson();
}
