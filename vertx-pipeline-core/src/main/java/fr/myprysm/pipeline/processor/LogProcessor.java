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

import fr.myprysm.pipeline.util.Alias;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static io.reactivex.Completable.complete;

/**
 * This processor logs its input according to the configured log level.
 * <p>
 * This processor is useful during Component development phase to see what is your input
 * or output at any position in the pipeline chain.
 * <p>
 * It can also be plugged for any tracing purpose in your pipeline chain.
 * <p>
 * Default log {@link org.slf4j.event.Level} is set to <code>DEBUG</code>
 */
@Alias(prefix = "pipeline-core", name = "log-processor")
public class LogProcessor extends BaseJsonProcessor<LogProcessorOptions> {
    private static final Logger LOG = LoggerFactory.getLogger(LogProcessor.class);
    private static final String MESSAGE_TPL = "Message: {}";

    private LogProcessorOptions options;
    private Level level;


    @Override
    public Single<JsonObject> transform(JsonObject input) {
        this.log(input);
        return Single.just(input);
    }

    private void log(JsonObject input) {
        switch (level) {
            case ERROR:
                error(MESSAGE_TPL, input);
                break;
            case WARN:
                warn(MESSAGE_TPL, input);
                break;
            case INFO:
                info(MESSAGE_TPL, input);
                break;
            case TRACE:
                trace(MESSAGE_TPL, input);
                break;
            case DEBUG:
            default:
                debug(MESSAGE_TPL, input);
                break;
        }
    }

    @Override
    protected Completable startVerticle() {
        return complete();
    }

    @Override
    public Completable configure(LogProcessorOptions config) {
        options = config;
        level = options.getLevel();
        return complete();
    }

    @Override
    public LogProcessorOptions readConfiguration(JsonObject config) {
        return new LogProcessorOptions(config);
    }

    @Override
    protected Logger delegate() {
        return LOG;
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return LogProcessorOptionsValidation.validate(config);
    }
}
