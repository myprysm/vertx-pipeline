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
import fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.ResponseType;
import fr.myprysm.pipeline.util.Alias;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Map;

import static fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.ResponseType.LIST_EXTRACT_FIRST;
import static fr.myprysm.pipeline.util.JsonHelpers.*;

@Alias(prefix = "pipeline-core", name = "http-get-request-processor")
public class HttpGetRequestProcessor extends BaseJsonProcessor<HttpGetRequestProcessorConfigurer> {
    private static final Logger LOG = LoggerFactory.getLogger(HttpGetRequestProcessor.class);

    private WebClient client;
    private String injection;
    private ResponseType responseType;

    private CaseInsensitiveHeaders defaultHeaders = new CaseInsensitiveHeaders();
    private CaseInsensitiveHeaders headers = new CaseInsensitiveHeaders();

    private CaseInsensitiveHeaders defaultQueryParams = new CaseInsensitiveHeaders();
    private CaseInsensitiveHeaders queryParams = new CaseInsensitiveHeaders();

    private LinkedList<String> urlFragments = new LinkedList<>();
    private CaseInsensitiveHeaders pathParams = new CaseInsensitiveHeaders();
    private OnError onError;

    @Override
    public Single<JsonObject> transform(JsonObject input) {
        return Single.fromCallable(() -> {
            HttpRequest<Buffer> request = client.get(getUrl(input));
            configureHeaders(request, input);
            configureQueryParams(request, input);

            return request.as(BodyCodec.string());
        }).flatMap(HttpRequest::rxSend)
                .flatMap(this::handleResponse)
                .map(body -> inject(body, input))
                .onErrorResumeNext(this.handleRequestError(input));


    }

    /**
     * Handles request common errors on building parameters or getting any other resource error
     *
     * @param input the input event
     * @return the input when processor is set in <code>CONTINUE</code> mode
     */
    private Function<Throwable, Single<JsonObject>> handleRequestError(JsonObject input) {
        return (Throwable throwable) -> {
            if (throwable instanceof DiscardableEventException) {
                error("Request error: {}", ((DiscardableEventException) throwable).getEvent());
            }
            error("Trace: ", throwable);

            if (onError == OnError.DISCARD) {
                return Single.error(throwable instanceof DiscardableEventException ? throwable : new DiscardableEventException(throwable, input));
            }

            return Single.just(input);
        };
    }

    /**
     * Craft the url with parameters to extract from input event.
     *
     * @param input the input event
     * @return the url
     */
    private String getUrl(JsonObject input) {
        if (urlFragments.size() == 1) {
            return urlFragments.get(0);
        }

        StringBuilder builder = new StringBuilder();
        for (String fragment : urlFragments) {
            if (fragment.startsWith("/")) {
                builder.append(fragment);
            } else {
                String paramValue = extractValue(pathParams.get(fragment), input);
                builder.append(paramValue);
            }
        }

        return builder.toString();
    }

    /**
     * Prepare headers for incoming request.
     * <p>
     * Appends all default headers (with no $event detected during configuration),
     * then search and extract all the remaining headers from input event.
     *
     * @param request the request
     * @param input   the input event
     */
    private void configureHeaders(HttpRequest<Buffer> request, JsonObject input) {
        CaseInsensitiveHeaders finalHeaders = new CaseInsensitiveHeaders();
        if (!defaultHeaders.isEmpty()) {
            finalHeaders.addAll(defaultHeaders);
        }

        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers) {
                finalHeaders.add(header.getKey(), extractValue(header.getValue(), input));
            }
        }

        if (!finalHeaders.isEmpty()) {
            request.headers().addAll(new MultiMap(finalHeaders));
        }
    }

    /**
     * Prepare query parameters for incoming request.
     * <p>
     * Appends all default query parameters (with no $event detected during configuration),
     * then search and extract all the remaining query parameters from input event.
     *
     * @param request the request
     * @param input   the input event
     */
    private void configureQueryParams(HttpRequest<Buffer> request, JsonObject input) {
        CaseInsensitiveHeaders finalQueryParams = new CaseInsensitiveHeaders();
        if (!defaultQueryParams.isEmpty()) {
            finalQueryParams.addAll(defaultQueryParams);
        }

        if (!queryParams.isEmpty()) {
            for (Map.Entry<String, String> param : queryParams) {
                finalQueryParams.add(param.getKey(), extractValue(param.getValue(), input));
            }
        }

        if (!finalQueryParams.isEmpty()) {
            request.queryParams().addAll(new MultiMap(finalQueryParams));
        }
    }

    /**
     * Extract the value as a string from input object at given path.
     * <p>
     * throw
     *
     * @param path  the path to extract
     * @param input the input event
     * @return the value at path as a string
     */
    private String extractValue(String path, JsonObject input) {
        return extractObject(input, path)
                .map(Object::toString)
                .orElseThrow(() -> new DiscardableEventException(obj()
                        .put("error", "unable to extract a value")
                        .put("path", path)
                        .put("input", input)
                ));
    }


    /**
     * Handle the response.
     * <p>
     * When status is not between 200 and 299, an error is emitted.
     *
     * @param response the response
     * @return the response as string
     */
    private Single<String> handleResponse(HttpResponse<String> response) {
        if (response.statusCode() < 200 || response.statusCode() > 299) {
            return Single.error(new DiscardableEventException(toJsonError(response)));
        }

        return Single.just(response.body());
    }


    /**
     * Inject the response at the requested path.
     * <p>
     * Copies the input before extracting response string body as requested element from configuration
     *
     * @param body  the response body as string
     * @param input the input event to inject the response
     * @return the input event with the response injected
     */
    private JsonObject inject(String body, JsonObject input) {
        JsonObject output = input.copy();
        Object value;
        switch (responseType) {
            case LIST:
            case LIST_EXTRACT_FIRST:
                value = asList(body, responseType);
                break;
            case OBJECT:
                value = asObject(body);
                break;
            case LONG:
                value = asLong(body);
                break;
            case DOUBLE:
                value = asDouble(body);
                break;
            case STRING:
            default:
                value = body;
        }

        writeObject(output, injection, value);

        return output;
    }

    /**
     * Transforms the incoming string body as either a list or the first item of this list.
     *
     * @param body the body as string
     * @param type the element to extract
     * @return the element extracted
     */
    private Object asList(String body, ResponseType type) {
        try {
            JsonArray array = new JsonArray(body);
            if (type == LIST_EXTRACT_FIRST && array.size() > 0) {
                return array.getValue(0);
            }
            return array;
        } catch (DecodeException exc) {
            throw new DiscardableEventException(exc);
        }
    }

    /**
     * Transforms the incoming string body as an object
     *
     * @param body the body as string
     * @return the element extracted
     */
    private JsonObject asObject(String body) {
        try {
            return new JsonObject(body);
        } catch (DecodeException exc) {
            throw new DiscardableEventException(exc);
        }
    }

    /**
     * Transforms the incoming string body as a long
     *
     * @param body the body as string
     * @return the element extracted
     */
    private Long asLong(String body) {
        try {
            return Long.parseLong(body);
        } catch (NumberFormatException exc) {
            throw new DiscardableEventException(exc);
        }
    }

    /**
     * Transforms the incoming string body as a double
     *
     * @param body the body as string
     * @return the element extracted
     */
    private Double asDouble(String body) {
        try {
            return Double.parseDouble(body);
        } catch (NullPointerException | NumberFormatException exc) {
            throw new DiscardableEventException(exc);
        }
    }

    /**
     * Transforms the incoming response into an error
     *
     * @param response the response
     * @return the error
     */
    private JsonObject toJsonError(HttpResponse<String> response) {
        return obj()
                .put("error", "Request failed")
                .put("status", response.statusCode())
                .put("statusMessage", response.statusMessage());
    }

    @Override
    protected Completable startVerticle() {
        return Completable.complete();
    }

    @Override
    public HttpGetRequestProcessorConfigurer readConfiguration(JsonObject config) {
        return new HttpGetRequestProcessorConfigurer(config);
    }

    @Override
    public Completable configure(HttpGetRequestProcessorConfigurer config) {
        client = WebClient.create(vertx, config.getClientOptions());
        injection = config.getInjection();
        responseType = config.getResponseType();
        defaultHeaders = config.getDefaultHeaders();
        headers = config.getExtractHeaders();
        defaultQueryParams = config.getDefaultQueryParams();
        queryParams = config.getExtractQueryParams();
        urlFragments = config.getUrlFragments();
        pathParams = config.getExtractPathParams();
        onError = config.getOnError();

        return Completable.complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return HttpGetRequestProcessorOptionsValidation.validate(config);
    }

    @Override
    protected Logger delegate() {
        return LOG;
    }
}
