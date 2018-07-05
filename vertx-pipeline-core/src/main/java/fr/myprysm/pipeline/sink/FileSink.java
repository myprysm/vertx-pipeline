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

package fr.myprysm.pipeline.sink;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import fr.myprysm.pipeline.sink.FileSinkOptions.Format;
import fr.myprysm.pipeline.sink.FileSinkOptions.Mode;
import fr.myprysm.pipeline.util.Alias;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.Pump;
import io.vertx.reactivex.FlowableHelper;
import io.vertx.reactivex.core.file.AsyncFile;
import io.vertx.reactivex.core.file.FileSystem;

import java.util.List;

import static io.reactivex.Completable.complete;
import static io.reactivex.Completable.defer;
import static strman.Strman.ensureRight;


/**
 * Write the incoming items in a file.
 * Each item is serialized according to the provided format.
 * <p>
 * Both formats <code>JSON</code> and <code>YAML</code> are supported.
 * <p>
 * Whenever the sink cannot write into the destination file
 * an <code>UNRECOVERABLE</code> signal is sent to shutdown the pipeline.
 */
@Alias(prefix = "pipeline-core", name = "file-sink")
public class FileSink extends FlushableJsonSink<FileSinkOptions> implements FlowableOnSubscribe<JsonObject> {

    private FileSystem fs;
    private ObjectMapper mapper;
    private String fullPath;

    private Disposable disposable;
    private FlowableEmitter<JsonObject> emitter;
    private AsyncFile asyncFile;
    private Integer batchSize;
    private Format format;
    private String path;
    private String file;
    private Mode mode;

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
                .buffer(batchSize)
                .subscribe(this::write);
        emitter.setDisposable(disposable);
        return complete();
    }

    @Override
    public Completable shutdown() {
        emitter.onComplete();
        disposable.dispose();
        return asyncFile.rxFlush().andThen(defer(asyncFile::rxClose));
    }

    /**
     * Writes the buffer of objects collected from the event bus.
     * <p>
     * Serializes the list by concatenating all the elements with a new line.
     *
     * @param objects the buffer of objects.
     */
    private void write(List<JsonObject> objects) {
        Flowable<Buffer> items = Flowable.fromIterable(objects)
                .map(object -> mapper.writeValueAsString(object) + "\n")
                .map(Buffer::buffer);
        Pump.pump(FlowableHelper.toReadStream(items), asyncFile.getDelegate()).start();
    }

    @Override
    public Completable configure(FileSinkOptions config) {
        fs = vertx.fileSystem();
        batchSize = config.getBatchSize();
        format = config.getFormat();
        mode = config.getMode();
        path = config.getPath();
        file = config.getFile();
        fullPath = ensureRight(path, "/") + file + "." + format.toString();


        return folderExists()
                .andThen(defer(this::fileCheck))
                .andThen(defer(this::prepareMapper));
    }

    private Completable prepareMapper() {
        switch (format) {
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

        switch (mode) {
            case append:
                openOpts.setAppend(true);
                break;
            case overwrite:
                openOpts.setTruncateExisting(true);
                break;
            case fail:
            default:
                openOpts.setCreateNew(true);
                break;
        }

        return fs.rxOpen(fullPath, openOpts).flatMapCompletable(async -> {
            asyncFile = async;
            return complete();
        });
    }

    private Completable folderExists() {
        return fs.rxExists(path)
                .flatMapCompletable(exists -> exists ? complete() : fs.rxMkdirs(path, "rwxr-x---"));
    }

    @Override
    public Integer batchSize() {
        return batchSize;
    }

    @Override
    public Completable flush() {
        return asyncFile.rxFlush();
    }

    @Override
    public void subscribe(FlowableEmitter<JsonObject> emitter) throws Exception {
        this.emitter = emitter;
    }


}
