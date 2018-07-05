require 'vertx/util/utils.rb'
# Generated from fr.myprysm.pipeline.pipeline.PipelineService
module VertxPipelineCore
  #  The Pipeline Service is the centric service.
  #  <p>
  #  It provides the capability
  class PipelineService
    # @private
    # @param j_del [::VertxPipelineCore::PipelineService] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxPipelineCore::PipelineService] the underlying java delegate
    def j_del
      @j_del
    end
    @@j_api_type = Object.new
    def @@j_api_type.accept?(obj)
      obj.class == PipelineService
    end
    def @@j_api_type.wrap(obj)
      PipelineService.new(obj)
    end
    def @@j_api_type.unwrap(obj)
      obj.j_del
    end
    def self.j_api_type
      @@j_api_type
    end
    def self.j_class
      Java::FrMyprysmPipelinePipeline::PipelineService.java_class
    end
    #  Get the nodes available
    # @yield 
    # @return [void]
    def get_nodes
      if block_given?
        return @j_del.java_method(:getNodes, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ::Vertx::Util::Utils.to_set(ar.result).map! { |elt| elt } : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling get_nodes()"
    end
    #  Get the running pipelines across all the instances.
    # @yield the handler
    # @return [void]
    def get_running_pipelines
      if block_given?
        return @j_del.java_method(:getRunningPipelines, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ::Vertx::Util::Utils.to_set(ar.result).map! { |elt| elt != nil ? JSON.parse(elt.toJson.encode) : nil } : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling get_running_pipelines()"
    end
    #  Get the description of the pipeline identified by the provided deployment information.
    # @param [Hash] deployment the deployment information
    # @yield the handler
    # @return [void]
    def get_pipeline_description(deployment=nil)
      if deployment.class == Hash && block_given?
        return @j_del.java_method(:getPipelineDescription, [Java::FrMyprysmPipelinePipeline::PipelineDeployment.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::FrMyprysmPipelinePipeline::PipelineDeployment.new(::Vertx::Util::Utils.to_json_object(deployment)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling get_pipeline_description(#{deployment})"
    end
    #  Starts a pipeline with the provided configuration.
    #  <p>
    #  Please note that the pipeline name must be unique across all the instances.
    #  <p>
    #  When running in cluster mode, the service will try to find an appropriate node to start the pipeline.
    #  This allows to run now data flows from nodes that are not currently hosting the components.
    #  <p>
    #  Response contains the normalized name with the control channel to communicate through signals
    #  with the deployed pipeline.
    # @param [Hash] options the pipeline configuration
    # @param [String] node the node to start the pipeline. can be null.
    # @yield the handler
    # @return [void]
    def start_pipeline(options=nil,node=nil)
      if options.class == Hash && node.class == String && block_given?
        return @j_del.java_method(:startPipeline, [Java::FrMyprysmPipelinePipeline::PipelineOptions.java_class,Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::FrMyprysmPipelinePipeline::PipelineOptions.new(::Vertx::Util::Utils.to_json_object(options)),node,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling start_pipeline(#{options},#{node})"
    end
    #  Stops the pipeline from the provided deployment.
    #  <p>
    #  Emits a signal when operation is complete.
    # @param [Hash] deployment the deployment information of the pipeline to stop.
    # @yield the handler
    # @return [void]
    def stop_pipeline(deployment=nil)
      if deployment.class == Hash && block_given?
        return @j_del.java_method(:stopPipeline, [Java::FrMyprysmPipelinePipeline::PipelineDeployment.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::FrMyprysmPipelinePipeline::PipelineDeployment.new(::Vertx::Util::Utils.to_json_object(deployment)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling stop_pipeline(#{deployment})"
    end
  end
end
