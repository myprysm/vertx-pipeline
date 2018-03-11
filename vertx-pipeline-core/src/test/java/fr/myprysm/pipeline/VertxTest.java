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

package fr.myprysm.pipeline;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static fr.myprysm.pipeline.util.JsonHelpers.EMPTY_STRING;

/**
 * Base Vertx test class.
 * It enables the {@link VertxExtension} to run tests with {@link io.vertx.core.Vertx}
 * and Junit 5
 */
@ExtendWith(VertxExtension.class)
public interface VertxTest {

    Logger LOG = LoggerFactory.getLogger(VertxTest.class);
    Map<String, String> RESOURCES = new HashMap<>();

    static String stringFromFile(String path) {
        try {
            return readFileFromClassPath(path);
        } catch (Exception e) {
            LOG.error("Unable to load {} as JsonObject...", path);
            LOG.error("Reason: ", e);
            return EMPTY_STRING;
        }
    }

    static JsonObject objectFromFile(String path) {
        try {
            return new JsonObject(readFileFromClassPath(path));
        } catch (Exception e) {
            LOG.error("Unable to load {} as JsonObject...", path);
            LOG.error("Reason: ", e);
            return new JsonObject();
        }
    }

    static JsonArray arrayFromFile(String path) {
        try {
            return new JsonArray(readFileFromClassPath(path));
        } catch (Exception e) {
            LOG.error("Unable to load {} as JsonArray...", path);
            LOG.error("Reason: ", e);
            return new JsonArray();
        }
    }

    /**
     * Read the Event Bus console file from classpath resources and cache it for further calls to avoid reading resource everytime.
     * File is extracted manually to avoid vertx file cache to occur as feature can be explicitly disabled by user.
     *
     * @return the Event Bus console file as a string
     */
    static String readFileFromClassPath(String path) throws IOException {
        if (!RESOURCES.containsKey(path)) {
            RESOURCES.put(path, readFromInputStream(ClassLoader.getSystemResourceAsStream(path)));
        }

        return RESOURCES.get(path);
    }

    /**
     * Extract text as String from the provided input stream.
     *
     * @param inputStream the stream to read text
     * @return the text extracted from the input stream
     * @throws IOException If any error occurs. Should never happen as only call
     */
    static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}
