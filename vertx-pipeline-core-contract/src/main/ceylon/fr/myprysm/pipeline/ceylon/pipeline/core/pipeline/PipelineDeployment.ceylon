import fr.myprysm.pipeline.pipeline {
  PipelineDeployment_=PipelineDeployment
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
/* Generated from fr.myprysm.pipeline.pipeline.PipelineDeployment */
" Response provided when a pipeline is successfully deployed.\n"
shared class PipelineDeployment(
  " The pipeline control channel address.\n <p>\n This address allows to communicate signals to this particular pipeline.\n <p>\n See vertx-pipeline-core about supported <code>Signal</code>s.\n"
  shared String? controlChannel = null,
  " The pipeline name.\n <p>\n This is a normalized name (kebab-cased).\n"
  shared String? name = null) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = JsonObject();
    if (exists controlChannel) {
      json.put("controlChannel", controlChannel);
    }
    if (exists name) {
      json.put("name", name);
    }
    return json;
  }
}

shared object pipelineDeployment {

  shared PipelineDeployment fromJson(JsonObject json) {
    String? controlChannel = json.getStringOrNull("controlChannel");
    String? name = json.getStringOrNull("name");
    return PipelineDeployment {
      controlChannel = controlChannel;
      name = name;
    };
  }

  shared object toCeylon extends Converter<PipelineDeployment_, PipelineDeployment>() {
    shared actual PipelineDeployment convert(PipelineDeployment_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<PipelineDeployment, PipelineDeployment_>() {
    shared actual PipelineDeployment_ convert(PipelineDeployment src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = PipelineDeployment_(json);
      return ret;
    }
  }
  shared JsonObject toJson(PipelineDeployment obj) => obj.toJson();
}
