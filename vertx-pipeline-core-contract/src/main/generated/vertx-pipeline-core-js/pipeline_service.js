/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/** @module vertx-pipeline-core-js/pipeline_service */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JPipelineService = Java.type('fr.myprysm.pipeline.pipeline.PipelineService');
var PipelineDeployment = Java.type('fr.myprysm.pipeline.pipeline.PipelineDeployment');
var PipelineOptions = Java.type('fr.myprysm.pipeline.pipeline.PipelineOptions');

/**
 The Pipeline Service is the centric service.
 <p>
 It provides the capability

 @class
*/
var PipelineService = function(j_val) {

  var j_pipelineService = j_val;
  var that = this;

  /**
   Get the nodes available

   @public
   @param handler {function} 
   */
  this.getNodes = function(handler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_pipelineService["getNodes(io.vertx.core.Handler)"](function(ar) {
      if (ar.succeeded()) {
        handler(utils.convReturnSet(ar.result()), null);
      } else {
        handler(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Get the running pipelines across all the instances.

   @public
   @param handler {function} the handler 
   */
  this.getRunningPipelines = function(handler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_pipelineService["getRunningPipelines(io.vertx.core.Handler)"](function(ar) {
      if (ar.succeeded()) {
        handler(utils.convReturnListSetDataObject(ar.result()), null);
      } else {
        handler(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Get the description of the pipeline identified by the provided deployment information.

   @public
   @param deployment {Object} the deployment information 
   @param handler {function} the handler 
   */
  this.getPipelineDescription = function(deployment, handler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_pipelineService["getPipelineDescription(fr.myprysm.pipeline.pipeline.PipelineDeployment,io.vertx.core.Handler)"](deployment != null ? new PipelineDeployment(new JsonObject(Java.asJSONCompatible(deployment))) : null, function(ar) {
      if (ar.succeeded()) {
        handler(utils.convReturnDataObject(ar.result()), null);
      } else {
        handler(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Starts a pipeline with the provided configuration.
   <p>
   Please note that the pipeline name must be unique across all the instances.
   <p>
   When running in cluster mode, the service will try to find an appropriate node to start the pipeline.
   This allows to run now data flows from nodes that are not currently hosting the components.
   <p>
   Response contains the normalized name with the control channel to communicate through signals
   with the deployed pipeline.

   @public
   @param options {Object} the pipeline configuration 
   @param node {string} the node to start the pipeline. can be null. 
   @param handler {function} the handler 
   */
  this.startPipeline = function(options, node, handler) {
    var __args = arguments;
    if (__args.length === 3 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'string' && typeof __args[2] === 'function') {
      j_pipelineService["startPipeline(fr.myprysm.pipeline.pipeline.PipelineOptions,java.lang.String,io.vertx.core.Handler)"](options != null ? new PipelineOptions(new JsonObject(Java.asJSONCompatible(options))) : null, node, function(ar) {
      if (ar.succeeded()) {
        handler(utils.convReturnDataObject(ar.result()), null);
      } else {
        handler(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Stops the pipeline from the provided deployment.
   <p>
   Emits a signal when operation is complete.

   @public
   @param deployment {Object} the deployment information of the pipeline to stop. 
   @param handler {function} the handler 
   */
  this.stopPipeline = function(deployment, handler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_pipelineService["stopPipeline(fr.myprysm.pipeline.pipeline.PipelineDeployment,io.vertx.core.Handler)"](deployment != null ? new PipelineDeployment(new JsonObject(Java.asJSONCompatible(deployment))) : null, function(ar) {
      if (ar.succeeded()) {
        handler(null, null);
      } else {
        handler(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_pipelineService;
};

PipelineService._jclass = utils.getJavaClass("fr.myprysm.pipeline.pipeline.PipelineService");
PipelineService._jtype = {
  accept: function(obj) {
    return PipelineService._jclass.isInstance(obj._jdel);
  },
  wrap: function(jdel) {
    var obj = Object.create(PipelineService.prototype, {});
    PipelineService.apply(obj, arguments);
    return obj;
  },
  unwrap: function(obj) {
    return obj._jdel;
  }
};
PipelineService._create = function(jdel) {
  var obj = Object.create(PipelineService.prototype, {});
  PipelineService.apply(obj, arguments);
  return obj;
}
module.exports = PipelineService;