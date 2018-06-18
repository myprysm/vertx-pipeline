package fr.myprysm.pipeline.ceylon.pipeline.core.pipeline;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;
import com.redhat.ceylon.compiler.java.metadata.TypeParameter;
import com.redhat.ceylon.compiler.java.metadata.TypeParameters;
import com.redhat.ceylon.compiler.java.metadata.Variance;
import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.java.runtime.model.ReifiedType;
import ceylon.language.Callable;
import ceylon.language.DocAnnotation$annotation$;
import java.util.List;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@Ceylon(major = 8)
@DocAnnotation$annotation$(description = " The Pipeline Service is the centric service.\n <p>\n It provides the capability\n")
public class PipelineService implements ReifiedType {

  @Ignore
  public static final io.vertx.lang.ceylon.ConverterFactory<fr.myprysm.pipeline.pipeline.PipelineService, PipelineService> TO_CEYLON = new io.vertx.lang.ceylon.ConverterFactory<fr.myprysm.pipeline.pipeline.PipelineService, PipelineService>() {
    public io.vertx.lang.ceylon.Converter<fr.myprysm.pipeline.pipeline.PipelineService, PipelineService> converter(final TypeDescriptor... descriptors) {
      return new io.vertx.lang.ceylon.Converter<fr.myprysm.pipeline.pipeline.PipelineService, PipelineService>() {
        public PipelineService convert(fr.myprysm.pipeline.pipeline.PipelineService src) {
          return new PipelineService(src);
        }
      };
    }
  };

  @Ignore
  public static final io.vertx.lang.ceylon.Converter<PipelineService, fr.myprysm.pipeline.pipeline.PipelineService> TO_JAVA = new io.vertx.lang.ceylon.Converter<PipelineService, fr.myprysm.pipeline.pipeline.PipelineService>() {
    public fr.myprysm.pipeline.pipeline.PipelineService convert(PipelineService src) {
      return src.delegate;
    }
  };

  @Ignore public static final TypeDescriptor $TypeDescriptor$ = new io.vertx.lang.ceylon.VertxTypeDescriptor(TypeDescriptor.klass(PipelineService.class), fr.myprysm.pipeline.pipeline.PipelineService.class, TO_JAVA, TO_CEYLON);
  @Ignore private final fr.myprysm.pipeline.pipeline.PipelineService delegate;

  public PipelineService(fr.myprysm.pipeline.pipeline.PipelineService delegate) {
    this.delegate = delegate;
  }

  @Ignore 
  public TypeDescriptor $getType$() {
    return $TypeDescriptor$;
  }

  @Ignore
  public Object getDelegate() {
    return delegate;
  }

  @DocAnnotation$annotation$(description = " Get the running pipelines across all the instances.\n <p>\n This is a complete description of all the pipeline with their options.\n Please take care when using this as when to many pipelines are deployed\n this can lead either to an `java.lang.OutOfMemoryError` or to a communication failure\n as the description is too large to be emitted through the event bus.\n")
  @TypeInfo("ceylon.language::Anything")
  public void getRunningPipelines(
    final @TypeInfo("ceylon.language::Anything(ceylon.language::Throwable|ceylon.language::List<fr.myprysm.pipeline.ceylon.pipeline.core.pipeline::PipelineOptions>)") @Name("handler")@DocAnnotation$annotation$(description = "the handler\n") Callable<?> handler) {
    io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.List<fr.myprysm.pipeline.pipeline.PipelineOptions>>> arg_0 = handler == null ? null : new io.vertx.lang.ceylon.CallableAsyncResultHandler<java.util.List<fr.myprysm.pipeline.pipeline.PipelineOptions>>(handler) {
      public Object toCeylon(java.util.List<fr.myprysm.pipeline.pipeline.PipelineOptions> event) {
        return io.vertx.lang.ceylon.ToCeylon.convertList(fr.myprysm.pipeline.ceylon.pipeline.core.pipeline.PipelineOptions.$TypeDescriptor$, event, fr.myprysm.pipeline.ceylon.pipeline.core.pipeline.pipelineOptions_.get_().getToCeylon());
      }
    };
    delegate.getRunningPipelines(arg_0);
  }

  @DocAnnotation$annotation$(description = " Starts a pipeline with the provided configuration.\n <p>\n Please note that the pipeline name must be unique across all the instances.\n <p>\n When running in cluster mode, the service will try to find an appropriate node to start the pipeline.\n This allows to run now data flows from nodes that are not currently hosting the components.\n <p>\n Response contains the normalized name with the control channel to communicate through signals\n with the deployed pipeline.\n")
  @TypeInfo("ceylon.language::Anything")
  public void startPipeline(
    final @TypeInfo("fr.myprysm.pipeline.ceylon.pipeline.core.pipeline::PipelineOptions") @Name("options")@DocAnnotation$annotation$(description = "the pipeline configuration\n") fr.myprysm.pipeline.ceylon.pipeline.core.pipeline.PipelineOptions options, 
    final @TypeInfo("ceylon.language::Anything(ceylon.language::Throwable|fr.myprysm.pipeline.ceylon.pipeline.core.pipeline::PipelineDeployment)") @Name("handler")@DocAnnotation$annotation$(description = "the handler\n") Callable<?> handler) {
    fr.myprysm.pipeline.pipeline.PipelineOptions arg_0 = options == null ? null : new fr.myprysm.pipeline.pipeline.PipelineOptions(io.vertx.lang.ceylon.ToJava.JsonObject.convert(options.toJson()));
    io.vertx.core.Handler<io.vertx.core.AsyncResult<fr.myprysm.pipeline.pipeline.PipelineDeployment>> arg_1 = handler == null ? null : new io.vertx.lang.ceylon.CallableAsyncResultHandler<fr.myprysm.pipeline.pipeline.PipelineDeployment>(handler) {
      public Object toCeylon(fr.myprysm.pipeline.pipeline.PipelineDeployment event) {
        return fr.myprysm.pipeline.ceylon.pipeline.core.pipeline.pipelineDeployment_.get_().getToCeylon().safeConvert(event);
      }
    };
    delegate.startPipeline(arg_0, arg_1);
  }

  @DocAnnotation$annotation$(description = " Stops the pipeline identified by the provided name.\n <p>\n Emits a signal when operation is complete.\n")
  @TypeInfo("ceylon.language::Anything")
  public void stopPipeline(
    final @TypeInfo("ceylon.language::String") @Name("name")@DocAnnotation$annotation$(description = "the name of the pipeline to stop.\n") ceylon.language.String name, 
    final @TypeInfo("ceylon.language::Anything(ceylon.language::Throwable?)") @Name("handler")@DocAnnotation$annotation$(description = "the handler\n") Callable<?> handler) {
    java.lang.String arg_0 = io.vertx.lang.ceylon.ToJava.String.safeConvert(name);
    io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> arg_1 = handler == null ? null : new io.vertx.lang.ceylon.CallableAsyncResultHandler<java.lang.Void>(handler) {
      public Object toCeylon(java.lang.Void event) {
        return null;
      }
    };
    delegate.stopPipeline(arg_0, arg_1);
  }

}
