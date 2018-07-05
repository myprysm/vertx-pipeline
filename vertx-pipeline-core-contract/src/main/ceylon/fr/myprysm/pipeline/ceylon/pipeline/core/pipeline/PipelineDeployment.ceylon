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
  " The pipeline control channel address.\n <p>\n This address allows to communicate signals to this particular pipeline and its components.\n <p>\n See vertx-pipeline-core about supported <code>Signal</code>s.\n"
  shared String? controlChannel = null,
  " The pipeline identifier.\n <p>\n This is basically the deployment ID for the <code>PipelineVerticle</code>.\n <p>\n This is a read-only property\n"
  shared String? id = null,
  " The pipeline name.\n <p>\n This is a normalized name (kebab-cased).\n"
  shared String? name = null,
  " The pipeline node identifier.\n <p>\n This is the node that hosts the pipeline in cluster mode.\n"
  shared String? node = null) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = JsonObject();
    if (exists controlChannel) {
      json.put("controlChannel", controlChannel);
    }
    if (exists id) {
      json.put("id", id);
    }
    if (exists name) {
      json.put("name", name);
    }
    if (exists node) {
      json.put("node", node);
    }
    return json;
  }
}

shared object pipelineDeployment {

  shared PipelineDeployment fromJson(JsonObject json) {
    String? controlChannel = json.getStringOrNull("controlChannel");
    String? id = json.getStringOrNull("id");
    String? name = json.getStringOrNull("name");
    String? node = json.getStringOrNull("node");
    return PipelineDeployment {
      controlChannel = controlChannel;
      id = id;
      name = name;
      node = node;
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
