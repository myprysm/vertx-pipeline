import fr.myprysm.pipeline.ceylon.pipeline.core.sink {
  FlushableSinkOptions
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
  FileSinkOptions_=FileSinkOptions
}
import ceylon.collection {
  HashMap
}
import io.vertx.core.json {
  JsonObject_=JsonObject,
  JsonArray_=JsonArray
}
/* Generated from fr.myprysm.pipeline.sink.FileSinkOptions */
shared class FileSinkOptions(
  Integer? batchSize = null,
  " The file name without extension.\n <p>\n Path and file will be tested on startup to detect whether the `fr.myprysm.pipeline.sink.FileSink` can write.\n <p>\n Defaults to <code>output</code>\n"
  shared String? file = null,
  " The format output of the `fr.myprysm.pipeline.sink.FileSink`.\n <p>\n <code>JSON</code> and <code>YAML</code> are supported.\n"
  shared String? format = null,
  " The mode of the `fr.myprysm.pipeline.sink.FileSink`\n"
  shared String? mode = null,
  String? name = null,
  " The path to store the output.\n <p>\n Path and file will be tested on startup to detect whether the `fr.myprysm.pipeline.sink.FileSink` can write.\n <p>\n Defaults to <code>/tmp</code>\n"
  shared String? path = null,
  String? type = null) extends FlushableSinkOptions(
  batchSize,
  name,
  type) satisfies BaseDataObject {
  shared actual default JsonObject toJson() {
    value json = super.toJson();
    if (exists file) {
      json.put("file", file);
    }
    if (exists format) {
      json.put("format", format);
    }
    if (exists mode) {
      json.put("mode", mode);
    }
    if (exists path) {
      json.put("path", path);
    }
    return json;
  }
}

shared object fileSinkOptions {

  shared FileSinkOptions fromJson(JsonObject json) {
    Integer? batchSize = json.getIntegerOrNull("batchSize");
    String? file = json.getStringOrNull("file");
    String? format = json.getStringOrNull("format");
    String? mode = json.getStringOrNull("mode");
    String? name = json.getStringOrNull("name");
    String? path = json.getStringOrNull("path");
    String? type = json.getStringOrNull("type");
    return FileSinkOptions {
      batchSize = batchSize;
      file = file;
      format = format;
      mode = mode;
      name = name;
      path = path;
      type = type;
    };
  }

  shared object toCeylon extends Converter<FileSinkOptions_, FileSinkOptions>() {
    shared actual FileSinkOptions convert(FileSinkOptions_ src) {
      value json = parse(src.toJson().string);
      assert(is JsonObject json);
      return fromJson(json);
    }
  }

  shared object toJava extends Converter<FileSinkOptions, FileSinkOptions_>() {
    shared actual FileSinkOptions_ convert(FileSinkOptions src) {
      // Todo : make optimized version without json
      value json = JsonObject_(src.toJson().string);
      value ret = FileSinkOptions_(json);
      return ret;
    }
  }
  shared JsonObject toJson(FileSinkOptions obj) => obj.toJson();
}
