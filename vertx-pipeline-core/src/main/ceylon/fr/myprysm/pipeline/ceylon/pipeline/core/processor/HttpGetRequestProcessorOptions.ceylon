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
  HttpGetRequestProcessorOptions_=HttpGetRequestProcessorOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions */
shared class HttpGetRequestProcessorOptions(
  " The headers to send with the request\n <p>\n This is a map of strings that will be automatically added as request headers on each request the processor will send.\n <p>\n To use data from current event, start the path to your value with <code>$event</code> (e.g. <code>$event.path.to.the.field</code>)\n <p>\n Allowed data are only primitive types and their respective lists.\n <p>\n This can be used to provide some authentication token.\n"
  shared JsonObject? headers = null,
  " The remote host\n"
  shared String? host = null,
  " The field to inject the response into\n <p>\n Once request is made it seems quite obvious that if the request responds data you will somehow use it.\n By using <code>injection</code> you will be able to write response data into this field in the current event.\n <p>\n If you don't need the response, you can use the keyword <code>none</code>, the response data will be automatically discarded.\n <p>\n Defaults to <code>response</code>\n"
  shared String? injection = null,
  Integer? instances = null,
  String? name = null,
  " Behaviour on error.\n <p>\n Can be one of:\n <ul>\n <li>CONTINUE</li>\n <li>DISCARD</li>\n </ul>\n"
  shared String? onError = null,
  " The path parameters\n <p>\n Parameters will be put in place of matching placeholders in the url.\n <p>\n Example :\n <p>\n URL: <code>/resource/:foo/route/:bar/values</code>\n <p>\n Path parameters: <pre>\n pathParams:\n   foo: $event.foo.id\n   bar: with\n </pre>\n <p>\n Called url will be: <code>/some/parameters/route/with/values</code>\n <p>\n To use data from current event, start the path to your value with <code>$event</code> (e.g. <code>$event.path.to.the.field</code>)\n <p>\n Allowed data are only primitive types.\n"
  shared JsonObject? pathParams = null,
  " The connection port to remote\n <p>\n If port is set to default, then it will automatically switch from port <code>80</code> to port <code>443</code>\n when <code>HTTPS</code> is enabled.\n"
  shared Integer? port = null,
  " The protocol to use\n <p>\n Can be either <code>HTTP</code> or <code>HTTPS</code>.\n If no port is provided, then it will automatically switch from port <code>80</code> to port <code>443</code>.\n"
  shared String? protocol = null,
  " The query parameters\n <p>\n Parameters will be added as <code>paramKey=paramValue</code> to the url.\n <p>\n To use data from current event, start the path to your value with <code>$event</code> (e.g. <code>$event.path.to.the.field</code>)\n <p>\n Allowed data are only primitive types and their respective lists.\n"
  shared JsonObject? queryParams = null,
  " The response type\n <p>\n Used for response deserialization, can be one of:\n <ul>\n <li><code>LIST</code></li>\n <li><code>OBJECT</code></li>\n <li><code>LONG</code></li>\n <li><code>DOUBLE</code></li>\n <li><code>STRING</code></li>\n </ul>\n <p>\n Defaults to <code>OBJECT</code>.\n"
  shared String? responseType = null,
  String? type = null,
  " The remote url\n <p>\n Used in combination with host, will represent the <code>absolute URI</code> to the remote server.\n <p>\n This url can contain path parameters that will be replaced automatically with  values extracted from the current event.\n Path parameters are url fragments beginning with <code>:</code> (column).\n <p>\n URL examples :\n <ul>\n <li><code>/some/url</code></li>\n <li><code>/some/:parameter/url</code></li>\n <li><code>/</code></li>\n </ul>\n <p>\n To use data from current event, start the path to your value with <code>$event</code> (e.g. <code>$event.path.to.the.field</code>)\n <p>\n Allowed data are only primitive types and their respective lists.\n"
  shared String? url = null,
  " The user agent to request\n <p>\n Sets an arbitrary user agent to request the remote URL.\n <p>\n Defaults to <code>vertx-pipeline/1.0</code>\n"
  shared String? userAgent = null) extends ProcessorOptions(
  instances,
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists headers) {
      json.put("headers", headers);
    }
    if (exists host) {
      json.put("host", host);
    }
    if (exists injection) {
      json.put("injection", injection);
    }
    if (exists onError) {
      json.put("onError", onError);
    }
    if (exists pathParams) {
      json.put("pathParams", pathParams);
    }
    if (exists port) {
      json.put("port", port);
    }
    if (exists protocol) {
      json.put("protocol", protocol);
    }
    if (exists queryParams) {
      json.put("queryParams", queryParams);
    }
    if (exists responseType) {
      json.put("responseType", responseType);
    }
    if (exists url) {
      json.put("url", url);
    }
    if (exists userAgent) {
      json.put("userAgent", userAgent);
    }
    return json;
  }
}

shared object httpGetRequestProcessorOptions {

  shared HttpGetRequestProcessorOptions fromJson(JsonObject json) {
    JsonObject? headers = json.getObjectOrNull("headers");
    String? host = json.getStringOrNull("host");
    String? injection = json.getStringOrNull("injection");
    Integer? instances = json.getIntegerOrNull("instances");
    String? name = json.getStringOrNull("name");
    String? onError = json.getStringOrNull("onError");
    JsonObject? pathParams = json.getObjectOrNull("pathParams");
    Integer? port = json.getIntegerOrNull("port");
    String? protocol = json.getStringOrNull("protocol");
    JsonObject? queryParams = json.getObjectOrNull("queryParams");
    String? responseType = json.getStringOrNull("responseType");
    String? type = json.getStringOrNull("type");
    String? url = json.getStringOrNull("url");
    String? userAgent = json.getStringOrNull("userAgent");
    return HttpGetRequestProcessorOptions {
      headers = headers;
      host = host;
      injection = injection;
      instances = instances;
      name = name;
      onError = onError;
      pathParams = pathParams;
      port = port;
      protocol = protocol;
      queryParams = queryParams;
      responseType = responseType;
      type = type;
      url = url;
      userAgent = userAgent;
    };
  }

  shared object toCeylon extends Converter<HttpGetRequestProcessorOptions_, HttpGetRequestProcessorOptions>() {
    shared actual HttpGetRequestProcessorOptions convert(HttpGetRequestProcessorOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<HttpGetRequestProcessorOptions, HttpGetRequestProcessorOptions_>() {
    shared actual HttpGetRequestProcessorOptions_ convert(HttpGetRequestProcessorOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = HttpGetRequestProcessorOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(HttpGetRequestProcessorOptions obj) => obj.toJson();
}
