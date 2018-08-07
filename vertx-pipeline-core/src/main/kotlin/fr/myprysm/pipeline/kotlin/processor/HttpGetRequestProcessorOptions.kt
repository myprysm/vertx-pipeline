package fr.myprysm.pipeline.kotlin.processor

import fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions
import fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.OnError
import fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.Protocol
import fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.ResponseType

fun HttpGetRequestProcessorOptions(
        headers: io.vertx.core.json.JsonObject? = null,
        host: String? = null,
        injection: String? = null,
        instances: Int? = null,
        name: String? = null,
        onError: OnError? = null,
        pathParams: io.vertx.core.json.JsonObject? = null,
        port: Int? = null,
        protocol: Protocol? = null,
        queryParams: io.vertx.core.json.JsonObject? = null,
        responseType: ResponseType? = null,
        type: String? = null,
        url: String? = null,
        userAgent: String? = null): HttpGetRequestProcessorOptions = fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions().apply {

    if (headers != null) {
        this.setHeaders(headers)
    }
    if (host != null) {
        this.setHost(host)
    }
    if (injection != null) {
        this.setInjection(injection)
    }
    if (instances != null) {
        this.setInstances(instances)
    }
    if (name != null) {
        this.setName(name)
    }
    if (onError != null) {
        this.setOnError(onError)
    }
    if (pathParams != null) {
        this.setPathParams(pathParams)
    }
    if (port != null) {
        this.setPort(port)
    }
    if (protocol != null) {
        this.setProtocol(protocol)
    }
    if (queryParams != null) {
        this.setQueryParams(queryParams)
    }
    if (responseType != null) {
        this.setResponseType(responseType)
    }
    if (type != null) {
        this.setType(type)
    }
    if (url != null) {
        this.setUrl(url)
    }
    if (userAgent != null) {
        this.setUserAgent(userAgent)
    }
}

