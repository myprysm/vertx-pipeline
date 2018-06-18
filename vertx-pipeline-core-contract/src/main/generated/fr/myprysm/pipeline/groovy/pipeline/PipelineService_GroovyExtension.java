package fr.myprysm.pipeline.groovy.pipeline;
public class PipelineService_GroovyExtension {
  public static void getRunningPipelines(fr.myprysm.pipeline.pipeline.PipelineService j_receiver, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.List<java.util.Map<String, Object>>>> handler) {
    j_receiver.getRunningPipelines(handler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.List<fr.myprysm.pipeline.pipeline.PipelineOptions>>>() {
      public void handle(io.vertx.core.AsyncResult<java.util.List<fr.myprysm.pipeline.pipeline.PipelineOptions>> ar) {
        handler.handle(ar.map(event -> event != null ? event.stream().map(elt -> elt != null ? io.vertx.core.impl.ConversionHelper.fromJsonObject(elt.toJson()) : null).collect(java.util.stream.Collectors.toList()) : null));
      }
    } : null);
  }
  public static void startPipeline(fr.myprysm.pipeline.pipeline.PipelineService j_receiver, java.util.Map<String, Object> options, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> handler) {
    j_receiver.startPipeline(options != null ? new fr.myprysm.pipeline.pipeline.PipelineOptions(io.vertx.core.impl.ConversionHelper.toJsonObject(options)) : null,
      handler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<fr.myprysm.pipeline.pipeline.PipelineDeployment>>() {
      public void handle(io.vertx.core.AsyncResult<fr.myprysm.pipeline.pipeline.PipelineDeployment> ar) {
        handler.handle(ar.map(event -> event != null ? io.vertx.core.impl.ConversionHelper.fromJsonObject(event.toJson()) : null));
      }
    } : null);
  }
}
