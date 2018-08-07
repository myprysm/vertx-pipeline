package fr.myprysm.pipeline.kotlin.sink

import fr.myprysm.pipeline.sink.FileSinkOptions
import fr.myprysm.pipeline.sink.FileSinkOptions.Format
import fr.myprysm.pipeline.sink.FileSinkOptions.Mode

fun FileSinkOptions(
        batchSize: Int? = null,
        file: String? = null,
        format: Format? = null,
        mode: Mode? = null,
        name: String? = null,
        path: String? = null,
        type: String? = null): FileSinkOptions = fr.myprysm.pipeline.sink.FileSinkOptions().apply {

    if (batchSize != null) {
        this.setBatchSize(batchSize)
    }
    if (file != null) {
        this.setFile(file)
    }
    if (format != null) {
        this.setFormat(format)
    }
    if (mode != null) {
        this.setMode(mode)
    }
    if (name != null) {
        this.setName(name)
    }
    if (path != null) {
        this.setPath(path)
    }
    if (type != null) {
        this.setType(type)
    }
}

