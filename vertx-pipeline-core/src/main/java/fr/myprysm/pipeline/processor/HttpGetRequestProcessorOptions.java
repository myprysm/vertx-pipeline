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

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

import static fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.Protocol.HTTP;
import static fr.myprysm.pipeline.util.JsonHelpers.obj;

@DataObject(generateConverter = true)
public class HttpGetRequestProcessorOptions extends ProcessorOptions {

    public static final String EVENT = "$event";
    public static final String NOW = "$now";

    public static final Protocol DEFAULT_PROTOCOL = HTTP;
    public static final Integer DEFAULT_PORT = 80;
    public static final String DEFAULT_HOST = "localhost";
    public static final JsonObject DEFAULT_HEADERS = obj()
            .put("Accept", "application/json")
            .put("Content-Type", "application/json; charset=UTF-8");
    public static final JsonObject DEFAULT_QUERY_PARAMS = obj();
    public static final String DEFAULT_INJECTION = "response";
    public static final JsonObject DEFAULT_PATH_PARAMS = obj();
    public static final String DEFAULT_URL = "/";
    public static final String DEFAULT_USER_AGENT = "vertx-pipeline/1.0";
    public static final ResponseType DEFAULT_TYPE = ResponseType.OBJECT;
    public static final OnError DEFAULT_ON_ERROR = OnError.DISCARD;

    public enum ResponseType {LIST, LIST_EXTRACT_FIRST, OBJECT, LONG, DOUBLE, STRING}

    public enum OnError {CONTINUE, DISCARD}

    public enum Protocol {
        HTTP("http", 80), HTTPS("https", 443);

        private final String protocol;
        private final int port;

        Protocol(String protocol, int port) {
            this.protocol = protocol;
            this.port = port;
        }

        public String protocol() {
            return protocol;
        }

        public Integer port() {
            return port;
        }
    }

    private Protocol protocol = DEFAULT_PROTOCOL;
    private Integer port = DEFAULT_PORT;
    private String host = DEFAULT_HOST;
    private JsonObject headers = DEFAULT_HEADERS;
    private String url = DEFAULT_URL;
    private JsonObject queryParams = DEFAULT_QUERY_PARAMS;
    private JsonObject pathParams = DEFAULT_PATH_PARAMS;
    private String injection = DEFAULT_INJECTION;
    private String userAgent = DEFAULT_USER_AGENT;
    private ResponseType responseType = DEFAULT_TYPE;
    private OnError onError = DEFAULT_ON_ERROR;


    public HttpGetRequestProcessorOptions() {
        super();
    }

    public HttpGetRequestProcessorOptions(HttpGetRequestProcessorOptions other) {
        super(other);
        protocol = other.protocol;
        port = other.port;
        host = other.host;
        headers = other.headers;
        url = other.url;
        queryParams = other.queryParams;
        pathParams = other.pathParams;
        injection = other.injection;
        userAgent = other.userAgent;
        responseType = other.responseType;
        onError = other.onError;
    }

    public HttpGetRequestProcessorOptions(ProcessorOptions other) {
        super(other);
    }

    public HttpGetRequestProcessorOptions(JsonObject json) {
        super(json);
        HttpGetRequestProcessorOptionsConverter.fromJson(json, this);
    }

    /**
     * The protocol to use
     *
     * @return the protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * The protocol to use
     * <p>
     * Can be either <code>HTTP</code> or <code>HTTPS</code>.
     * If no port is provided, then it will automatically switch from port <code>80</code> to port <code>443</code>.
     *
     * @param protocol the protocol
     * @return this
     */
    public HttpGetRequestProcessorOptions setProtocol(Protocol protocol) {
        this.protocol = protocol;
        return this;
    }

    /**
     * The connection port to remote
     *
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * The connection port to remote
     * <p>
     * If port is set to default, then it will automatically switch from port <code>80</code> to port <code>443</code>
     * when <code>HTTPS</code> is enabled.
     *
     * @param port the port to reach remote
     * @return this
     */
    public HttpGetRequestProcessorOptions setPort(Integer port) {
        this.port = port;
        return this;
    }

    /**
     * The remote host
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * The remote host
     *
     * @param host the remote host
     * @return this
     */
    public HttpGetRequestProcessorOptions setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * The headers to send with the request
     *
     * @return the headers
     */
    public JsonObject getHeaders() {
        return headers;
    }

    /**
     * The headers to send with the request
     * <p>
     * This is a map of strings that will be automatically added as request headers on each request the processor will send.
     * <p>
     * To use data from current event, start the path to your value with <code>$event</code> (e.g. <code>$event.path.to.the.field</code>)
     * <p>
     * Allowed data are only primitive types and their respective lists.
     * <p>
     * This can be used to provide some authentication token.
     *
     * @param headers the headers
     * @return this
     */
    public HttpGetRequestProcessorOptions setHeaders(JsonObject headers) {
        this.headers = headers;
        return this;
    }

    /**
     * The remote url
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * The remote url
     * <p>
     * Used in combination with host, will represent the <code>absolute URI</code> to the remote server.
     * <p>
     * This url can contain path parameters that will be replaced automatically with  values extracted from the current event.
     * Path parameters are url fragments beginning with <code>:</code> (column).
     * <p>
     * URL examples :
     * <ul>
     * <li><code>/some/url</code></li>
     * <li><code>/some/:parameter/url</code></li>
     * <li><code>/</code></li>
     * </ul>
     * <p>
     * To use data from current event, start the path to your value with <code>$event</code> (e.g. <code>$event.path.to.the.field</code>)
     * <p>
     * Allowed data are only primitive types and their respective lists.
     *
     * @param url the remote url
     * @return this
     */
    public HttpGetRequestProcessorOptions setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * The path parameters
     *
     * @return the path parameters
     */
    public JsonObject getPathParams() {
        return pathParams;
    }

    /**
     * The path parameters
     * <p>
     * Parameters will be put in place of matching placeholders in the url.
     * <p>
     * Example :
     * <p>
     * URL: <code>/resource/:foo/route/:bar/values</code>
     * <p>
     * Path parameters: <pre>
     * pathParams:
     *   foo: $event.foo.id
     *   bar: with
     * </pre>
     * <p>
     * Called url will be: <code>/some/parameters/route/with/values</code>
     * <p>
     * To use data from current event, start the path to your value with <code>$event</code> (e.g. <code>$event.path.to.the.field</code>)
     * <p>
     * Allowed data are only primitive types.
     *
     * @param pathParams the path parameters
     * @return this
     */
    public HttpGetRequestProcessorOptions setPathParams(JsonObject pathParams) {
        this.pathParams = pathParams;
        return this;
    }

    /**
     * The query parameters
     *
     * @return the query parameters
     */
    public JsonObject getQueryParams() {
        return queryParams;
    }

    /**
     * The query parameters
     * <p>
     * Parameters will be added as <code>paramKey=paramValue</code> to the url.
     * <p>
     * To use data from current event, start the path to your value with <code>$event</code> (e.g. <code>$event.path.to.the.field</code>)
     * <p>
     * Allowed data are only primitive types and their respective lists.
     *
     * @param queryParams the query parameters
     * @return this
     */
    public HttpGetRequestProcessorOptions setQueryParams(JsonObject queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    /**
     * The field to inject the response into
     *
     * @return the field
     */
    public String getInjection() {
        return injection;
    }

    /**
     * The field to inject the response into
     * <p>
     * Once request is made it seems quite obvious that if the request responds data you will somehow use it.
     * By using <code>injection</code> you will be able to write response data into this field in the current event.
     * <p>
     * If you don't need the response, you can use the keyword <code>none</code>, the response data will be automatically discarded.
     * <p>
     * Defaults to <code>response</code>
     *
     * @param injection the field
     * @return this
     */
    public HttpGetRequestProcessorOptions setInjection(String injection) {
        this.injection = injection;
        return this;
    }

    /**
     * The user agent to request
     *
     * @return the user agent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * The user agent to request
     * <p>
     * Sets an arbitrary user agent to request the remote URL.
     * <p>
     * Defaults to <code>vertx-pipeline/1.0</code>
     *
     * @param userAgent the user agent
     * @return this
     */
    public HttpGetRequestProcessorOptions setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * The response type
     *
     * @return the response type
     */
    public ResponseType getResponseType() {
        return responseType;
    }

    /**
     * The response type
     * <p>
     * Used for response deserialization, can be one of:
     * <ul>
     * <li><code>LIST</code></li>
     * <li><code>OBJECT</code></li>
     * <li><code>LONG</code></li>
     * <li><code>DOUBLE</code></li>
     * <li><code>STRING</code></li>
     * </ul>
     * <p>
     * Defaults to <code>OBJECT</code>.
     *
     * @param responseType the response type
     */
    public HttpGetRequestProcessorOptions setResponseType(ResponseType responseType) {
        this.responseType = responseType;
        return this;
    }

    /**
     * Behaviour on error
     *
     * @return behaviour on error
     */
    public OnError getOnError() {
        return onError;
    }

    /**
     * Behaviour on error.
     * <p>
     * Can be one of:
     * <ul>
     * <li>CONTINUE</li>
     * <li>DISCARD</li>
     * </ul>
     *
     * @param onError the behaviour on error
     * @return this
     */
    public HttpGetRequestProcessorOptions setOnError(OnError onError) {
        this.onError = onError;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public HttpGetRequestProcessorOptions setName(String name) {
        return (HttpGetRequestProcessorOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public HttpGetRequestProcessorOptions setType(String type) {
        return (HttpGetRequestProcessorOptions) super.setType(type);
    }

    @Override
    public Integer getInstances() {
        return super.getInstances();
    }

    @Override
    public HttpGetRequestProcessorOptions setInstances(Integer instances) {
        return (HttpGetRequestProcessorOptions) super.setInstances(instances);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        HttpGetRequestProcessorOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HttpGetRequestProcessorOptions)) return false;
        if (!super.equals(o)) return false;
        HttpGetRequestProcessorOptions that = (HttpGetRequestProcessorOptions) o;
        return protocol == that.protocol &&
                Objects.equals(port, that.port) &&
                Objects.equals(host, that.host) &&
                Objects.equals(headers, that.headers) &&
                Objects.equals(url, that.url) &&
                Objects.equals(queryParams, that.queryParams) &&
                Objects.equals(pathParams, that.pathParams) &&
                Objects.equals(injection, that.injection) &&
                Objects.equals(userAgent, that.userAgent) &&
                responseType == that.responseType &&
                onError == that.onError;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), protocol, port, host, headers, url, queryParams, pathParams, injection, userAgent, responseType, onError);
    }

    @Override
    public String toString() {
        return "HttpGetRequestProcessorOptions{" +
                "protocol=" + protocol +
                ", port=" + port +
                ", host='" + host + '\'' +
                ", headers=" + headers +
                ", url='" + url + '\'' +
                ", queryParams=" + queryParams +
                ", pathParams=" + pathParams +
                ", injection='" + injection + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", responseType=" + responseType +
                ", onError=" + onError +
                '}';
    }
}
