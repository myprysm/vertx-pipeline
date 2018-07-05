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
import java.util.Set;
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

  @DocAnnotation$annotation$(description = " Get the nodes available\n")
  @TypeInfo("ceylon.language::Anything")
  public void getNodes(
    final @TypeInfo("ceylon.language::Anything(ceylon.language::Throwable|ceylon.language::Set<ceylon.language::String>)") @Name("handler") Callable<?> handler) {
    io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Set<java.lang.String>>> arg_0 = handler == null ? null : new io.vertx.lang.ceylon.CallableAsyncResultHandler<java.util.Set<java.lang.String>>(handler) {
      public Object toCeylon(java.util.Set<java.lang.String> event) {
        return io.vertx.lang.ceylon.ToCeylon.convertSet(ceylon.language.String.$TypeDescriptor$, event, io.vertx.lang.ceylon.ToCeylon.String);
      }
    };
    delegate.getNodes(arg_0);
  }

  @DocAnnotation$annotation$(description = " Get the running pipelines across all the instances.\n")
  @TypeInfo("ceylon.language::Anything")
  public void getRunningPipelines(
    final @TypeInfo("ceylon.language::Anything(ceylon.language::Throwable|ceylon.language::Set<fr.myprysm.pipeline.ceylon.pipeline.core.pipeline::PipelineDeployment>)") @Name("handler")@DocAnnotation$annotation$(description = "the handler\n") Callable<?> handler) {
    io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.Set<fr.myprysm.pipeline.pipeline.PipelineDeployment>>> arg_0 = handler == null ? null : new io.vertx.lang.ceylon.CallableAsyncResultHandler<java.util.Set<fr.myprysm.pipeline.pipeline.PipelineDeployment>>(handler) {
      public Object toCeylon(java.util.Set<fr.myprysm.pipeline.pipeline.PipelineDeployment> event) {
        return io.vertx.lang.ceylon.ToCeylon.convertSet(fr.myprysm.pipeline.ceylon.pipeline.core.pipeline.PipelineDeployment.$TypeDescriptor$, event, fr.myprysm.pipeline.ceylon.pipeline.core.pipeline.pipelineDeployment_.get_().getToCeylon());
      }
    };
    delegate.getRunningPipelines(arg_0);
  }

  @DocAnnotation$annotation$(description = " Get the description of the pipeline identified by the provided deployment information.\n")
  @TypeInfo("ceylon.language::Anything")
  public void getPipelineDescription(
    final @TypeInfo("fr.myprysm.pipeline.ceylon.pipeline.core.pipeline::PipelineDeployment") @Name("deployment")@DocAnnotation$annotation$(description = "the deployment information\n") fr.myprysm.pipeline.ceylon.pipeline.core.pipeline.PipelineDeployment deployment, 
    final @TypeInfo("ceylon.language::Anything(ceylon.language::Throwable|fr.myprysm.pipeline.ceylon.pipeline.core.pipeline::PipelineOptions)") @Name("handler")@DocAnnotation$annotation$(description = "the handler\n") Callable<?> handler) {
    fr.myprysm.pipeline.pipeline.PipelineDeployment arg_0 = deployment == null ? null : new fr.myprysm.pipeline.pipeline.PipelineDeployment(io.vertx.lang.ceylon.ToJava.JsonObject.convert(deployment.toJson()));
    io.vertx.core.Handler<io.vertx.core.AsyncResult<fr.myprysm.pipeline.pipeline.PipelineOptions>> arg_1 = handler == null ? null : new io.vertx.lang.ceylon.CallableAsyncResultHandler<fr.myprysm.pipeline.pipeline.PipelineOptions>(handler) {
      public Object toCeylon(fr.myprysm.pipeline.pipeline.PipelineOptions event) {
        return fr.myprysm.pipeline.ceylon.pipeline.core.pipeline.pipelineOptions_.get_().getToCeylon().safeConvert(event);
      }
    };
    delegate.getPipelineDescription(arg_0, arg_1);
  }

  @DocAnnotation$annotation$(description = " Starts a pipeline with the provided configuration.\n <p>\n Please note that the pipeline name must be unique across all the instances.\n <p>\n When running in cluster mode, the service will try to find an appropriate node to start the pipeline.\n This allows to run now data flows from nodes that are not currently hosting the components.\n <p>\n Response contains the normalized name with the control channel to communicate through signals\n with the deployed pipeline.\n")
  @TypeInfo("ceylon.language::Anything")
  public void startPipeline(
    final @TypeInfo("fr.myprysm.pipeline.ceylon.pipeline.core.pipeline::PipelineOptions") @Name("options")@DocAnnotation$annotation$(description = "the pipeline configuration\n") fr.myprysm.pipeline.ceylon.pipeline.core.pipeline.PipelineOptions options, 
    final @TypeInfo("ceylon.language::String") @Name("node")@DocAnnotation$annotation$(description = "the node to start the pipeline. can be null.\n") ceylon.language.String node, 
    final @TypeInfo("ceylon.language::Anything(ceylon.language::Throwable|fr.myprysm.pipeline.ceylon.pipeline.core.pipeline::PipelineDeployment)") @Name("handler")@DocAnnotation$annotation$(description = "the handler\n") Callable<?> handler) {
    fr.myprysm.pipeline.pipeline.PipelineOptions arg_0 = options == null ? null : new fr.myprysm.pipeline.pipeline.PipelineOptions(io.vertx.lang.ceylon.ToJava.JsonObject.convert(options.toJson()));
    java.lang.String arg_1 = io.vertx.lang.ceylon.ToJava.String.safeConvert(node);
    io.vertx.core.Handler<io.vertx.core.AsyncResult<fr.myprysm.pipeline.pipeline.PipelineDeployment>> arg_2 = handler == null ? null : new io.vertx.lang.ceylon.CallableAsyncResultHandler<fr.myprysm.pipeline.pipeline.PipelineDeployment>(handler) {
      public Object toCeylon(fr.myprysm.pipeline.pipeline.PipelineDeployment event) {
        return fr.myprysm.pipeline.ceylon.pipeline.core.pipeline.pipelineDeployment_.get_().getToCeylon().safeConvert(event);
      }
    };
    delegate.startPipeline(arg_0, arg_1, arg_2);
  }

  @DocAnnotation$annotation$(description = " Stops the pipeline from the provided deployment.\n <p>\n Emits a signal when operation is complete.\n")
  @TypeInfo("ceylon.language::Anything")
  public void stopPipeline(
    final @TypeInfo("fr.myprysm.pipeline.ceylon.pipeline.core.pipeline::PipelineDeployment") @Name("deployment")@DocAnnotation$annotation$(description = "the deployment information of the pipeline to stop.\n") fr.myprysm.pipeline.ceylon.pipeline.core.pipeline.PipelineDeployment deployment, 
    final @TypeInfo("ceylon.language::Anything(ceylon.language::Throwable?)") @Name("handler")@DocAnnotation$annotation$(description = "the handler\n") Callable<?> handler) {
    fr.myprysm.pipeline.pipeline.PipelineDeployment arg_0 = deployment == null ? null : new fr.myprysm.pipeline.pipeline.PipelineDeployment(io.vertx.lang.ceylon.ToJava.JsonObject.convert(deployment.toJson()));
    io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> arg_1 = handler == null ? null : new io.vertx.lang.ceylon.CallableAsyncResultHandler<java.lang.Void>(handler) {
      public Object toCeylon(java.lang.Void event) {
        return null;
      }
    };
    delegate.stopPipeline(arg_0, arg_1);
  }

}
