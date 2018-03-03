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

package fr.myprysm.pipeline.processor;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.slf4j.event.Level;

import java.util.Objects;

/**
 * Log Processor options.
 * <p>
 * They extend directly base {@link ProcessorOptions} and provide
 * the ability to set the acceptable level to log incoming messages.
 */
@DataObject(generateConverter = true)
public class LogProcessorOptions extends ProcessorOptions {
    public static final Level DEFAULT_LEVEL = Level.DEBUG;

    private Level level = DEFAULT_LEVEL;


    public LogProcessorOptions() {
        super();
    }

    public LogProcessorOptions(LogProcessorOptions other) {
        super(other);
        level = other.level;
    }

    public LogProcessorOptions(ProcessorOptions other) {
        super(other);
    }

    public LogProcessorOptions(JsonObject json) {
        super(json);
        LogProcessorOptionsConverter.fromJson(json, this);
    }

    /**
     * The log {@link Level} to write the incoming items.
     *
     * @return the log level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * The log {@link Level} to write the incoming items.
     * <p>
     * Defaults to <code>DEBUG</code>.
     * <p>
     * One of:
     * <ul>
     * <li><code>TRACE</code></li>
     * <li><code>DEBUG</code></li>
     * <li><code>INFO</code></li>
     * <li><code>WARN</code></li>
     * <li><code>ERROR</code></li>
     * </ul>
     *
     * @param level the log level to log items
     * @return this
     */
    public LogProcessorOptions setLevel(Level level) {
        this.level = level;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public LogProcessorOptions setName(String name) {
        return (LogProcessorOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public LogProcessorOptions setType(String type) {
        return (LogProcessorOptions) super.setType(type);
    }

    @Override
    public Integer getInstances() {
        return super.getInstances();
    }

    @Override
    public LogProcessorOptions setInstances(Integer instances) {
        return (LogProcessorOptions) super.setInstances(instances);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        LogProcessorOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogProcessorOptions)) return false;
        if (!super.equals(o)) return false;
        LogProcessorOptions that = (LogProcessorOptions) o;
        return level == that.level;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), level);
    }

    @Override
    public String toString() {
        return "LogProcessorOptions{" +
                "level=" + level +
                "} " + super.toString();
    }
}
