import fr.myprysm.pipeline.pump {
  EventBusPumpOptions_=EventBusPumpOptions
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
import fr.myprysm.pipeline.ceylon.pipeline.core.pump {
  PumpOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.pump.EventBusPumpOptions */
shared class EventBusPumpOptions(
  " The address to listen on the event bus.\n <p>\n The pump can only listen to one topic.\n"
  shared String? address = null,
  String? name = null,
  String? type = null) extends PumpOptions(
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists address) {
      json.put("address", address);
    }
    return json;
  }
}

shared object eventBusPumpOptions {

  shared EventBusPumpOptions fromJson(JsonObject json) {
    String? address = json.getStringOrNull("address");
    String? name = json.getStringOrNull("name");
    String? type = json.getStringOrNull("type");
    return EventBusPumpOptions {
      address = address;
      name = name;
      type = type;
    };
  }

  shared object toCeylon extends Converter<EventBusPumpOptions_, EventBusPumpOptions>() {
    shared actual EventBusPumpOptions convert(EventBusPumpOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<EventBusPumpOptions, EventBusPumpOptions_>() {
    shared actual EventBusPumpOptions_ convert(EventBusPumpOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = EventBusPumpOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(EventBusPumpOptions obj) => obj.toJson();
}
