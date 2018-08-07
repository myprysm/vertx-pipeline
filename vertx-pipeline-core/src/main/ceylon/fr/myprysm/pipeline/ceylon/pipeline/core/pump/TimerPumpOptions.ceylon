import fr.myprysm.pipeline.pump {
  TimerPumpOptions_=TimerPumpOptions
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
/* Generated from fr.myprysm.pipeline.pump.TimerPumpOptions */
shared class TimerPumpOptions(
  " The custom data to add with the tick\n <p>\n It can be any arbitrary json/yaml data.\n <p>\n No additional data is sent when it is null or empty\n"
  shared JsonObject? data = null,
  " The interval of the pump.\n <p>\n The value must be a positive integer.\n <p>\n The pump will emit a message every tick containing the\n current counter and timestamp.\n"
  shared Integer? interval = null,
  String? name = null,
  String? type = null,
  " The time unit of the pump.\n <p>\n The value is one of \n"
  shared String? unit = null) extends PumpOptions(
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists data) {
      json.put("data", data);
    }
    if (exists interval) {
      json.put("interval", interval);
    }
    if (exists unit) {
      json.put("unit", unit);
    }
    return json;
  }
}

shared object timerPumpOptions {

  shared TimerPumpOptions fromJson(JsonObject json) {
    JsonObject? data = json.getObjectOrNull("data");
    Integer? interval = json.getIntegerOrNull("interval");
    String? name = json.getStringOrNull("name");
    String? type = json.getStringOrNull("type");
    String? unit = json.getStringOrNull("unit");
    return TimerPumpOptions {
      data = data;
      interval = interval;
      name = name;
      type = type;
      unit = unit;
    };
  }

  shared object toCeylon extends Converter<TimerPumpOptions_, TimerPumpOptions>() {
    shared actual TimerPumpOptions convert(TimerPumpOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<TimerPumpOptions, TimerPumpOptions_>() {
    shared actual TimerPumpOptions_ convert(TimerPumpOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = TimerPumpOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(TimerPumpOptions obj) => obj.toJson();
}
