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
  CounterEmitterProcessorOptions_=CounterEmitterProcessorOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.processor.CounterEmitterProcessorOptions */
shared class CounterEmitterProcessorOptions(
  " The delay before sending <code>TERMINATE</code> signal.\n <p>\n A <code>FLUSH</code> signal is always emitted before sending\n <code>TERMINATE</code> thus setting delay between both emissions\n can help the pipeline finish his job properly, especially when accumulating data.\n"
  shared Integer? delayTerminate = null,
  Integer? instances = null,
  " The interval of the counter.\n <p>\n The value must be a positive integer.\n <p>\n The counter will emit a signal every <code>interval</code> event received.\n"
  shared Integer? interval = null,
  String? name = null,
  " The signal to emit every interval.\n <p>\n Can be one of <code>FLUSH</code> or <code>TERMINATE</code>\n"
  shared String? signal = null,
  String? type = null) extends ProcessorOptions(
  instances,
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists delayTerminate) {
      json.put("delayTerminate", delayTerminate);
    }
    if (exists interval) {
      json.put("interval", interval);
    }
    if (exists signal) {
      json.put("signal", signal);
    }
    return json;
  }
}

shared object counterEmitterProcessorOptions {

  shared CounterEmitterProcessorOptions fromJson(JsonObject json) {
    Integer? delayTerminate = json.getIntegerOrNull("delayTerminate");
    Integer? instances = json.getIntegerOrNull("instances");
    Integer? interval = json.getIntegerOrNull("interval");
    String? name = json.getStringOrNull("name");
    String? signal = json.getStringOrNull("signal");
    String? type = json.getStringOrNull("type");
    return CounterEmitterProcessorOptions {
      delayTerminate = delayTerminate;
      instances = instances;
      interval = interval;
      name = name;
      signal = signal;
      type = type;
    };
  }

  shared object toCeylon extends Converter<CounterEmitterProcessorOptions_, CounterEmitterProcessorOptions>() {
    shared actual CounterEmitterProcessorOptions convert(CounterEmitterProcessorOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<CounterEmitterProcessorOptions, CounterEmitterProcessorOptions_>() {
    shared actual CounterEmitterProcessorOptions_ convert(CounterEmitterProcessorOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = CounterEmitterProcessorOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(CounterEmitterProcessorOptions obj) => obj.toJson();
}
