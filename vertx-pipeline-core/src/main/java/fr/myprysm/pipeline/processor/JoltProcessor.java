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

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.chainr.ChainrBuilder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import fr.myprysm.pipeline.processor.JoltProcessorOptions.Format;
import fr.myprysm.pipeline.util.Alias;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Alias(prefix = "pipeline-core", name = "jolt-processor")
public class JoltProcessor extends BaseJsonProcessor<JoltProcessorOptions> {
    private static final Logger LOG = LoggerFactory.getLogger(JoltProcessor.class);
    private JsonArray specs;
    private Format format;
    private String path;
    private Chainr chainr;

    @Override
    public Single<JsonObject> transform(JsonObject input) {
        return Single.fromCallable(() -> {
            Map<String, Object> data = Json.mapper.convertValue(input.copy(), new TypeReference<Map<String, Object>>() {
            });
            return new JsonObject((Map<String, Object>) chainr.transform(data));
        });
    }


    @Override
    protected Completable startVerticle() {
        return Completable.complete();
    }

    @Override
    public JoltProcessorOptions readConfiguration(JsonObject config) {
        return new JoltProcessorOptions(config);
    }

    @Override
    public Completable configure(JoltProcessorOptions config) {
        specs = config.getSpecs();
        format = config.getFormat();
        path = config.getPath();
        return vertx.fileSystem().rxReadFile(path)
                .map(this::readSpecs)
                // In case we cannot read the file...
                .onErrorResumeNext(Single.just(specs))
                .flatMapCompletable(this::configureChainr);
    }

    private Completable configureChainr(JsonArray specs) {
        List<Map<String, Object>> rawSpecs = Json.mapper.convertValue(specs, new TypeReference<List<Map<String, Object>>>() {
        });

        chainr = new ChainrBuilder(rawSpecs).build();
        return Completable.complete();
    }

    /**
     * Reads the JOLT specs.
     * <p>
     * Throws an error when it cannot read the buffer as a {@link JsonArray}.
     *
     * @param buffer the specs from file
     * @return the specs as an array.
     */
    private JsonArray readSpecs(Buffer buffer) {
        if (LOG.isDebugEnabled()) {
            debug("Reading specs {}", buffer.toString());
        }

        ObjectMapper mapper;
        switch (format) {
            case yaml:
                mapper = new YAMLMapper().enable(JsonParser.Feature.ALLOW_COMMENTS);
                break;
            case json:
            default:
                mapper = Json.mapper;
        }

        try {
            return new JsonArray(mapper.readValue(buffer.toString(), List.class));
        } catch (Exception e) {
            error("Unable to read file: ", e);
            throw new DecodeException("Failed to decode:" + e.getMessage(), e);
        }
    }


    @Override
    public ValidationResult validate(JsonObject config) {
        return JoltProcessorOptionsValidation.validate(config);
    }

    @Override
    protected Logger delegate() {
        return LOG;
    }
}
