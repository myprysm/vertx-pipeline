import fr.myprysm.pipeline.pump {
  CronPumpOptions_=CronPumpOptions
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
/* Generated from fr.myprysm.pipeline.pump.CronPumpOptions */
shared class CronPumpOptions(
  " Quartz cron expression\n <p>\n The official documentation to write your own epression is available at\n <a href=\"http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html\">this address</a>.\n"
  shared String? cron = null,
  " The custom data to add with the tick\n <p>\n It can be any arbitrary json/yaml data.\n <p>\n No additional data is sent when it is null or empty\n"
  shared JsonObject? data = null,
  " The emitter class\n Provides the ability to execute custom job by providing a custom implementation of a <code>CronEmitter</code>\n <p>\n This can be useful to extract a dataset for batch process on a bunch of single items.\n <p>\n Defaults to <code>fr.myprysm.pipeline.pump.CronEmitter</code>\n"
  shared String? emitter = null,
  String? name = null,
  String? type = null) extends PumpOptions(
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists cron) {
      json.put("cron", cron);
    }
    if (exists data) {
      json.put("data", data);
    }
    if (exists emitter) {
      json.put("emitter", emitter);
    }
    return json;
  }
}

shared object cronPumpOptions {

  shared CronPumpOptions fromJson(JsonObject json) {
    String? cron = json.getStringOrNull("cron");
    JsonObject? data = json.getObjectOrNull("data");
    String? emitter = json.getStringOrNull("emitter");
    String? name = json.getStringOrNull("name");
    String? type = json.getStringOrNull("type");
    return CronPumpOptions {
      cron = cron;
      data = data;
      emitter = emitter;
      name = name;
      type = type;
    };
  }

  shared object toCeylon extends Converter<CronPumpOptions_, CronPumpOptions>() {
    shared actual CronPumpOptions convert(CronPumpOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<CronPumpOptions, CronPumpOptions_>() {
    shared actual CronPumpOptions_ convert(CronPumpOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = CronPumpOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(CronPumpOptions obj) => obj.toJson();
}
