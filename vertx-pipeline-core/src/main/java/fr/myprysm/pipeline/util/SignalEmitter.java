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

/**
 * Signal receiver to publish internal health check
 * and control across a pipeline from its components
 * to the controller and vice-versa
 */
public interface SignalEmitter extends Stream {

    /**
     * The control channel to emit signals.
     *
     * @return the control channel
     */
    String controlChannel();

    /**
     * Emits a signal
     *
     * @param signal the signal to emit
     */
    void emitSignal(Signal signal);
}
