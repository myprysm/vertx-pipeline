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

import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Map;

import static fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.Protocol.HTTP;
import static fr.myprysm.pipeline.processor.HttpGetRequestProcessorOptions.Protocol.HTTPS;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class HttpGetRequestProcessorConfigurer extends HttpGetRequestProcessorOptions {

    private static final Logger LOG = LoggerFactory.getLogger(HttpGetRequestProcessorConfigurer.class);

    private WebClientOptions options = new WebClientOptions();


    private CaseInsensitiveHeaders defaultHeaders = new CaseInsensitiveHeaders();
    private CaseInsensitiveHeaders extractHeaders = new CaseInsensitiveHeaders();

    private CaseInsensitiveHeaders defaultQueryParams = new CaseInsensitiveHeaders();
    private CaseInsensitiveHeaders extractQueryParams = new CaseInsensitiveHeaders();

    private LinkedList<String> urlFragments = new LinkedList<>();
    private CaseInsensitiveHeaders extractPathParams = new CaseInsensitiveHeaders();


    public HttpGetRequestProcessorConfigurer(HttpGetRequestProcessorOptions config) {
        super(config);
        build();
    }

    public HttpGetRequestProcessorConfigurer(JsonObject json) {
        super(json);
        build();
    }

    public WebClientOptions getClientOptions() {
        return options;
    }

    public CaseInsensitiveHeaders getDefaultHeaders() {
        return defaultHeaders;
    }

    public CaseInsensitiveHeaders getExtractHeaders() {
        return extractHeaders;
    }

    public CaseInsensitiveHeaders getDefaultQueryParams() {
        return defaultQueryParams;
    }

    public CaseInsensitiveHeaders getExtractQueryParams() {
        return extractQueryParams;
    }

    public LinkedList<String> getUrlFragments() {
        return urlFragments;
    }

    public CaseInsensitiveHeaders getExtractPathParams() {
        return extractPathParams;
    }

    private void build() {
        prepareClientOptions();
        preparePortAndProtocol();
        prepareHeaders();
        prepareQueryParams();
        prepareUrlFragments();
        preparePathParams();

    }

    private void preparePathParams() {
        JsonObject pathParams = getPathParams();
        for (Map.Entry<String, String> pathParam : extractPathParams) {
            // Remove start ":" (column)
            String param = pathParam.getKey();
            String field = pathParams.getString(param);

            if (field.contains(EVENT)) {
                field = field.substring(field.indexOf(".") + 1);
            }

            pathParam.setValue(field);
        }
    }

    private void prepareUrlFragments() {
        String[] urlParts = getUrl().split("/");
        StringBuilder part = new StringBuilder("/");
        if (urlParts.length == 0) {
            urlFragments.add(part.toString());
            return;
        }

        for (String urlPart : urlParts) {
            if (isNotBlank(urlPart)) {
                if (urlPart.startsWith(":")) {
                    // A path param, add current exact path to the fragments
                    // + the parameter without ":"
                    String pathParam = urlPart.substring(1);
                    extractPathParams.set(pathParam, "");
                    urlFragments.add(part.toString());
                    urlFragments.add(pathParam);
                    part = new StringBuilder("/");
                } else {
                    // Continue to concatenate the parts
                    part.append(urlPart).append("/");
                }
            }
        }

        // We are not on "/", remove trailing "/"
        if (part.charAt(part.length() - 1) == '/') {
            part.deleteCharAt(part.length() - 1);
        }

        if (part.length() > 0) {
            urlFragments.add(part.toString());
        }
    }

    private void prepareClientOptions() {
        options.setFollowRedirects(true)
                .setUserAgent(getUserAgent())
                .setDefaultHost(getHost());
    }

    private void preparePortAndProtocol() {
        Protocol protocol = getProtocol();
        Integer port = getPort();
        if (protocol == HTTPS) {
            options.setSsl(true);

            if (HTTP.port().equals(port) || HTTPS.port().equals(port)) {
                options.setDefaultPort(HTTPS.port());
            } else {
                options.setDefaultPort(port);
            }
        } else if (protocol == HTTP && !HTTP.port().equals(port)) {
            options.setDefaultPort(port);
        }
    }

    private void prepareHeaders() {
        prepareParams(getHeaders(), defaultHeaders, extractHeaders);
    }

    private void prepareQueryParams() {
        prepareParams(getQueryParams(), defaultQueryParams, extractQueryParams);
    }

    private void prepareParams(JsonObject jsonParams, CaseInsensitiveHeaders defaultParams, CaseInsensitiveHeaders extractParams) {
        for (Map.Entry<String, Object> param : jsonParams) {
            String key = param.getKey();
            String value = (String) param.getValue();
            if (value.contains(EVENT)) {
                extractParams.add(key, value.substring(value.indexOf(".") + 1));
            } else {
                defaultParams.add(key, value);
            }
        }
    }


}
