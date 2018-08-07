import fr.myprysm.pipeline {
  DeploymentVerticleOptions_=DeploymentVerticleOptions
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
/* Generated from fr.myprysm.pipeline.DeploymentVerticleOptions */
shared class DeploymentVerticleOptions(
  shared Boolean? jmxEnabled = null,
  shared Boolean? metrics = null) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = JsonObject();
    if (exists jmxEnabled) {
      json.put("jmxEnabled", jmxEnabled);
    }
    if (exists metrics) {
      json.put("metrics", metrics);
    }
    return json;
  }
}

shared object deploymentVerticleOptions {

  shared DeploymentVerticleOptions fromJson(JsonObject json) {
    Boolean? jmxEnabled = json.getBooleanOrNull("jmxEnabled");
    Boolean? metrics = json.getBooleanOrNull("metrics");
    return DeploymentVerticleOptions {
      jmxEnabled = jmxEnabled;
      metrics = metrics;
    };
  }

  shared object toCeylon extends Converter<DeploymentVerticleOptions_, DeploymentVerticleOptions>() {
    shared actual DeploymentVerticleOptions convert(DeploymentVerticleOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<DeploymentVerticleOptions, DeploymentVerticleOptions_>() {
    shared actual DeploymentVerticleOptions_ convert(DeploymentVerticleOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = DeploymentVerticleOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(DeploymentVerticleOptions obj) => obj.toJson();
}
