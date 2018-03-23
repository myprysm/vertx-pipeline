/*
 * Copyright 2018 the original author or the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.myprysm.pipeline.datasource;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.serviceproxy.ServiceExceptionMessageCodec;

public class DatasourceRegistryExceptionMessageCodec implements MessageCodec<DatasourceRegistryException, DatasourceRegistryException> {
    private ServiceExceptionMessageCodec delegate = new ServiceExceptionMessageCodec();

    @Override
    public void encodeToWire(Buffer buffer, DatasourceRegistryException e) {
        delegate.encodeToWire(buffer, e);
    }

    @Override
    public DatasourceRegistryException decodeFromWire(int pos, Buffer buffer) {
        return (DatasourceRegistryException) delegate.decodeFromWire(pos, buffer);
    }

    @Override
    public DatasourceRegistryException transform(DatasourceRegistryException exception) {
        return exception;
    }

    @Override
    public String name() {
        return "ServiceException";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
