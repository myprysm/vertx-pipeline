import fr.myprysm.pipeline.ceylon.pipeline.core.sink {
  SinkOptions
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
import fr.myprysm.pipeline.sink {
  EventBusSinkOptions_=EventBusSinkOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.sink.EventBusSinkOptions */
shared class EventBusSinkOptions(
  String? name = null,
  " Publication addresses.\n <p>\n Those publications are broadcasted to each registered consumer.\n"
  shared {String*}? publish = null,
  " Publication addresses.\n <p>\n Those publications are casted to the first registered consumer that get the message.\n"
  shared {String*}? send = null,
  String? type = null) extends SinkOptions(
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

shared object eventBusSinkOptions {

  shared EventBusSinkOptions fromJson(JsonObject json) {
    String? name = json.getStringOrNull("name");
    {String*}? publish = json.getArrayOrNull("publish")?.strings;
    {String*}? send = json.getArrayOrNull("send")?.strings;
    String? type = json.getStringOrNull("type");
    return EventBusSinkOptions {
      name = name;
      publish = publish;
      send = send;
      type = type;
    };
  }

  shared object toCeylon extends Converter<EventBusSinkOptions_, EventBusSinkOptions>() {
    shared actual EventBusSinkOptions convert(EventBusSinkOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<EventBusSinkOptions, EventBusSinkOptions_>() {
    shared actual EventBusSinkOptions_ convert(EventBusSinkOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = EventBusSinkOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(EventBusSinkOptions obj) => obj.toJson();
}
