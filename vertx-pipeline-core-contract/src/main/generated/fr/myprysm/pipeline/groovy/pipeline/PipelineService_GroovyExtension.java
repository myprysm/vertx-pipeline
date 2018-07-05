package fr.myprysm.pipeline.groovy.pipeline;
public class PipelineService_GroovyExtension {
  public static void getRunningPipelines(fr.myprysm.pipeline.pipeline.PipelineService j_receiver, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Set<java.util.Map<String, Object>>>> handler) {
    j_receiver.getRunningPipelines(handler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Set<fr.myprysm.pipeline.pipeline.PipelineDeployment>>>() {
      public void handle(io.vertx.core.AsyncResult<java.util.Set<fr.myprysm.pipeline.pipeline.PipelineDeployment>> ar) {
        handler.handle(ar.map(event -> event != null ? event.stream().map(elt -> elt != null ? io.vertx.core.impl.ConversionHelper.fromJsonObject(elt.toJson()) : null).collect(java.util.stream.Collectors.toSet()) : null));
      }
    } : null);
  }
  public static void getPipelineDescription(fr.myprysm.pipeline.pipeline.PipelineService j_receiver, java.util.Map<String, Object> deployment, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> handler) {
    j_receiver.getPipelineDescription(deployment != null ? new fr.myprysm.pipeline.pipeline.PipelineDeployment(io.vertx.core.impl.ConversionHelper.toJsonObject(deployment)) : null,
      handler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<fr.myprysm.pipeline.pipeline.PipelineOptions>>() {
      public void handle(io.vertx.core.AsyncResult<fr.myprysm.pipeline.pipeline.PipelineOptions> ar) {
        handler.handle(ar.map(event -> event != null ? io.vertx.core.impl.ConversionHelper.fromJsonObject(event.toJson()) : null));
      }
    } : null);
  }
  public static void startPipeline(fr.myprysm.pipeline.pipeline.PipelineService j_receiver, java.util.Map<String, Object> options, java.lang.String node, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Map<String, Object>>> handler) {
    j_receiver.startPipeline(options != null ? new fr.myprysm.pipeline.pipeline.PipelineOptions(io.vertx.core.impl.ConversionHelper.toJsonObject(options)) : null,
      node,
      handler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<fr.myprysm.pipeline.pipeline.PipelineDeployment>>() {
      public void handle(io.vertx.core.AsyncResult<fr.myprysm.pipeline.pipeline.PipelineDeployment> ar) {
        handler.handle(ar.map(event -> event != null ? io.vertx.core.impl.ConversionHelper.fromJsonObject(event.toJson()) : null));
      }
    } : null);
  }
  public static void stopPipeline(fr.myprysm.pipeline.pipeline.PipelineService j_receiver, java.util.Map<String, Object> deployment, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> handler) {
    j_receiver.stopPipeline(deployment != null ? new fr.myprysm.pipeline.pipeline.PipelineDeployment(io.vertx.core.impl.ConversionHelper.toJsonObject(deployment)) : null,
      handler != null ? new io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>>() {
      public void handle(io.vertx.core.AsyncResult<java.lang.Void> ar) {
        handler.handle(ar.map(event -> io.vertx.core.impl.ConversionHelper.fromObject(event)));
      }
    } : null);
  }
}
