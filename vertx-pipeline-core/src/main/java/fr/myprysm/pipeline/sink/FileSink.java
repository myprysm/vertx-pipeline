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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import fr.myprysm.pipeline.util.Flushable;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.file.AsyncFile;
import io.vertx.reactivex.core.file.FileSystem;

import java.util.List;

import static io.reactivex.Completable.complete;
import static io.reactivex.Completable.defer;
import static java.util.stream.Collectors.joining;


/**
 * Write the incoming items in a file.
 * Each item is serialized according to the provided format.
 * <p>
 * Both formats <code>JSON</code> and <code>YAML</code> are supported.
 * <p>
 * Whenever the sink cannot write into the destination file
 * an <code>UNRECOVERABLE</code> signal is sent to shutdown the pipeline.
 */
public class FileSink extends BaseJsonSink<FileSinkOptions> implements Flushable, FlowableOnSubscribe<JsonObject> {

    private FileSinkOptions options;
    private FileSystem fs;
    private ObjectMapper mapper;
    private String fullPath;

    private Disposable disposable;
    private FlowableEmitter<JsonObject> emitter;
    private AsyncFile file;

    @Override
    public void drain(JsonObject item) {
        emitter.onNext(item);
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return FileSinkOptionValidation.validate(config);
    }

    @Override
    public FileSinkOptions readConfiguration(JsonObject config) {
        return new FileSinkOptions(config);
    }

    @Override
    protected Completable startVerticle() {
        disposable = Flowable.create(this, BackpressureStrategy.BUFFER)
                .buffer(batchSize())
                .subscribe(this::write, this::handleError);
        emitter.setDisposable(disposable);
        return complete();
    }

    @Override
    public Completable shutdown() {
        disposable.dispose();
        return file.rxFlush().andThen(defer(file::rxClose));
    }

    /**
     * Writes the buffer of objects collected from the event bus.
     * <p>
     * Serializes the list by concatenating all the elements with a new line.
     *
     * @param objects the buffer of objects.
     */
    private void write(List<JsonObject> objects) {
        Buffer buffer = Buffer.buffer(objects.stream()
                .map(o -> {
                    try {
                        return mapper.writeValueAsString(o);
                    } catch (JsonProcessingException exc) {
                        // That should never happen.....
                        info("Error during " + options.getFormat().name() + " serialization.", exc);
                        return "{\"error\": \"serialization error\", \"message\": \"" + exc.getMessage().replaceAll("\"", "\\\"") + "\"}";
                    }
                }).collect(joining("\n")) + "\n"); // Ending line to concatenate each buffer when writing.

        file.write(buffer);
    }

    private void handleError(Throwable throwable) {
        error("Received an error: ", throwable);
    }

    @Override
    public Completable configure(FileSinkOptions config) {
        options = config;
        fullPath = config.getPath() + "/" + config.getFile() + "." + config.getFormat().toString();
        fs = vertx.fileSystem();

        return folderExists()
                .andThen(defer(this::fileCheck))
                .andThen(defer(this::prepareMapper));
    }

    private Completable prepareMapper() {
        switch (options.getFormat()) {
            case yaml:
                mapper = new YAMLMapper();
                break;
            case json:
            default:
                mapper = Json.mapper;
        }
        return complete();
    }

    private Completable fileCheck() {
        OpenOptions openOpts = new OpenOptions().setWrite(true).setCreate(true);

        switch (options.getMode()) {
            case append:
                openOpts.setAppend(true);
                break;
            case overwrite:
                openOpts.setTruncateExisting(true);
                break;
            case fail:
            default:
                openOpts.setCreateNew(true).setWrite(true);
                break;
        }

        return fs.rxOpen(fullPath, openOpts).flatMapCompletable(async -> {
            file = async;
            return complete();
        });
    }

    private Completable folderExists() {
        return fs.rxExists(options.getPath())
                .flatMapCompletable(exists -> exists ? complete() : fs.rxMkdirs(options.getPath(), "rwxr-x---"));
    }

    @Override
    public Integer batchSize() {
        return options.getBatchSize();
    }

    @Override
    public Completable flush() {
        return null;
    }

    @Override
    public void subscribe(FlowableEmitter<JsonObject> emitter) throws Exception {
        this.emitter = emitter;
    }
}
