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

import io.reactivex.Completable;

/**
 * Indicates that the object can be shut down.
 * <p>
 * A {@link ShutdownHook} provides a {@link #shutdown()} method.
 * This allows to stop the component as best as it can, releasing resources properly.
 */
public interface ShutdownHook {
    /**
     * Shuts down the object.
     * This is a hook to disconnect the pump of any external source before stopping ot.
     * Please note that regardless of the result the item will shut down.
     *
     * @return completable
     */
    Completable shutdown();
}
