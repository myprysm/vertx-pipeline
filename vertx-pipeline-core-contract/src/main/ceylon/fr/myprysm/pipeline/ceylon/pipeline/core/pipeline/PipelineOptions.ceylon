import fr.myprysm.pipeline.pipeline {
  PipelineOptions_=PipelineOptions
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
/* Generated from fr.myprysm.pipeline.pipeline.PipelineOptions */
shared class PipelineOptions(
  " The deploy channel.\n <p>\n This address is automatically generated when starting the pipeline\n"
  shared String? deployChannel = null,
  " The name of the pipeline.\n <p>\n This is used to build the pipeline components names.\n Expected to be camel-case (\"my-awesome-pipeline\").\n"
  shared String? name = null,
  " The processor set.\n Mandatory field for each processor is <code>type</code>.\n Name is automatically generated, you can provide your own one though.\n <p>\n See vertx-pipeline-core and other modules for more information about specific ProcessorOptions.\n"
  shared JsonArray? processors = null,
  " The pump.\n <p>\n Mandatory field is <code>type</code>.\n Name is automatically generated, you can provide your own one though.\n <p>\n See vertx-pipeline-core and other modules for more information about specific PumpOptions.\n"
  shared JsonObject? pump = null,
  " The sink.\n <p>\n Mandatory field is <code>type</code>.\n Name is automatically generated, you can provide your own one though.\n <p>\n See vertx-pipeline-core and other modules for more information about specific SinkOptions.\n"
  shared JsonObject? sink = null) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = JsonObject();
    if (exists deployChannel) {
      json.put("deployChannel", deployChannel);
    }
    if (exists name) {
      json.put("name", name);
    }
    if (exists processors) {
      json.put("processors", processors);
    }
    if (exists pump) {
      json.put("pump", pump);
    }
    if (exists sink) {
      json.put("sink", sink);
    }
    return json;
  }
}

shared object pipelineOptions {

  shared PipelineOptions fromJson(JsonObject json) {
    String? deployChannel = json.getStringOrNull("deployChannel");
    String? name = json.getStringOrNull("name");
    JsonArray? processors = json.getArrayOrNull("processors");
    JsonObject? pump = json.getObjectOrNull("pump");
    JsonObject? sink = json.getObjectOrNull("sink");
    return PipelineOptions {
      deployChannel = deployChannel;
      name = name;
      processors = processors;
      pump = pump;
      sink = sink;
    };
  }

  shared object toCeylon extends Converter<PipelineOptions_, PipelineOptions>() {
    shared actual PipelineOptions convert(PipelineOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<PipelineOptions, PipelineOptions_>() {
    shared actual PipelineOptions_ convert(PipelineOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = PipelineOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(PipelineOptions obj) => obj.toJson();
}
