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

package fr.myprysm.pipeline.processor;

import fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.OnError;
import fr.myprysm.pipeline.validation.JsonValidation;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.vertx.core.json.JsonObject;

import static fr.myprysm.pipeline.validation.JsonValidation.*;

public interface HttpGetRequestProcessorOptionsValidation {

    static ValidationResult validate(JsonObject config) {
        return
                validProtocol()
                        .and(validPort())
                        .and(validHost())
                        .and(validUrl())
                        .and(validInjection())
                        .and(validUserAgent())
                        .and(isNull("onError").or(isEnum("onError", OnError.class)))
                        .apply(config);
    }

    static JsonValidation validProtocol() {
        return isNull("protocol").or(isEnum("protocol", HttpGetRequestProcessorOptions.Protocol.class));
    }

    static JsonValidation validPort() {
        return isNull("port")
                .or(gte("port", 0L).and(lt("port", 65536L)));
    }

    static JsonValidation validHost() {
        return isNull("host").or(isString("host"));
    }

    static JsonValidation validUrl() {
        return isNull("url").or(isString("url"));
    }

    static JsonValidation validInjection() {
        return isNull("injection").or(isString("injection"));
    }

    static JsonValidation validUserAgent() {
        return isNull("userAgent").or(isString("userAgent"));
    }
}
