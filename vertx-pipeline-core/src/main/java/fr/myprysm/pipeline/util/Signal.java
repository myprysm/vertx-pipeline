/*
 * Copyright 2018 the original author or the original authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.myprysm.pipeline.util;

import fr.myprysm.pipeline.pipeline.PipelineVerticle;
import fr.myprysm.pipeline.processor.Processor;
import fr.myprysm.pipeline.pump.Pump;
import fr.myprysm.pipeline.sink.Sink;

/**
 * Signals that can emit any {@link Pump}, {@link Processor} or {@link Sink}
 * to its {@link PipelineVerticle} to trigger an action.
 */
public enum Signal {
    /**
     * Indicates that an error is <code>UNRECOVERABLE</code>,
     * meaning that the whole {@link PipelineVerticle} <b>must</b> shutdown.
     * <p>
     * This is mainly and usually emitted by a {@link Sink}
     * or a {@link Pump} during startup or runtime where
     * an issue make the whole {@link PipelineVerticle} unstable or at least unable
     * to process data in a proper way.
     */
    UNRECOVERABLE,
    /**
     * Indicates that the pipeline has finished his processing
     * and can therefore release its resources.
     * <p>
     * This is emitted by any kind of component to signal the whole pipeline has finished
     * his processing, thus resources can be released and {@link PipelineVerticle} can be undeployed.
     */
    TERMINATE,

    /**
     * Indicates to all the {@link fr.myprysm.pipeline.util.Flushable} processors
     */
    FLUSH,
    /**
     * <b>Not Implemented</b>, does currently nothing.
     * <p>
     * Sends an <code>INTERRUPT</code> signal to the pipeline, requesting {@link Pump}
     * to stop its emission, thus releasing its resource for some time,
     * without shutting down the whole pipeline.
     */
    INTERRUPT,
    /**
     * <b>Not Implemented</b>, does currently nothing.
     * <p>
     * Sends a <code>RESUME</code>signal to the pipeline, requesting {@link Pump}
     * to resume/restart its emission, thus producing new events.
     * <p>
     * This is the response to an <code>INTERRUPT</code> signal.
     */
    RESUME,

}
