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

package fr.myprysm.pipeline.sink;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject(generateConverter = true)
public class FileSinkOptions extends FlushableSinkOptions {
    public static final String DEFAULT_FILE = "output";
    public static final Format DEFAULT_FORMAT = Format.json;
    public static final String DEFAULT_PATH = "/tmp";
    public static final Mode DEFAULT_MODE = Mode.fail;

    public enum Format {
        json, yaml
    }

    public enum Mode {
        append, overwrite, fail
    }

    private String path = DEFAULT_PATH;
    private String file = DEFAULT_FILE;
    private Format format = DEFAULT_FORMAT;
    private Mode mode = DEFAULT_MODE;

    public FileSinkOptions() {
        super();
    }

    public FileSinkOptions(JsonObject json) {
        super(json);
        FileSinkOptionsConverter.fromJson(json, this);
    }

    public FileSinkOptions(FileSinkOptions other) {
        super(other);
        path = other.path;
        file = other.file;
        format = other.format;
        mode = other.mode;
    }

    public FileSinkOptions(SinkOptions other) {
        super(other);
    }

    /**
     * The path to store the output.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * The path to store the output.
     * <p>
     * Path and file will be tested on startup to detect whether the {@link FileSink} can write.
     * <p>
     * Defaults to <code>/tmp</code>
     *
     * @param path the path to store the output
     * @return this
     */
    public FileSinkOptions setPath(String path) {
        this.path = path;
        return this;
    }

    /**
     * The file name without extension
     *
     * @return the file name, without extension.
     */
    public String getFile() {
        return file;
    }

    /**
     * The file name without extension.
     * <p>
     * Path and file will be tested on startup to detect whether the {@link FileSink} can write.
     * <p>
     * Defaults to <code>output</code>
     *
     * @param file the file name, without extension.
     * @return this
     */
    public FileSinkOptions setFile(String file) {
        this.file = file;
        return this;
    }

    /**
     * The format output of the {@link FileSink}
     *
     * @return the format output
     */
    public Format getFormat() {
        return format;
    }


    /**
     * The format output of the {@link FileSink}.
     * <p>
     * <code>JSON</code> and <code>YAML</code> are supported.
     *
     * @param format the format
     * @return this
     */
    public FileSinkOptions setFormat(Format format) {
        this.format = format;
        return this;
    }

    /**
     * The mode of the {@link FileSink}
     *
     * @return the mode of the sink
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * The mode of the {@link FileSink}
     *
     * @param mode the mode of the sink
     * @return this
     */
    public FileSinkOptions setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    @Override
    public Integer getBatchSize() {
        return super.getBatchSize();
    }

    @Override
    public FileSinkOptions setBatchSize(Integer batchSize) {
        return (FileSinkOptions) super.setBatchSize(batchSize);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public FileSinkOptions setName(String name) {
        return (FileSinkOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public FileSinkOptions setType(String type) {
        return (FileSinkOptions) super.setType(type);
    }


    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        FileSinkOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileSinkOptions)) return false;
        if (!super.equals(o)) return false;
        FileSinkOptions that = (FileSinkOptions) o;
        return Objects.equals(path, that.path) &&
                Objects.equals(file, that.file) &&
                format == that.format &&
                mode == that.mode;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), path, file, format, mode);
    }

    @Override
    public String toString() {
        return "FileSinkOptions{" +
                "path='" + path + '\'' +
                ", file='" + file + '\'' +
                ", format=" + format +
                ", mode=" + mode +
                "} " + super.toString();
    }
}
