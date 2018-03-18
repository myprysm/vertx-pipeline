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

package fr.myprysm.pipeline.util;

import fr.myprysm.pipeline.ConsoleTest;
import fr.myprysm.pipeline.VertxTest;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class ConfigurableVerticleTest extends ConsoleTest implements VertxTest {

    @Test
    @DisplayName("Configurable verticle should log started and shutdown")
    void configurableVerticleShouldLogStartedAndShutdown(Vertx vertx, VertxTestContext ctx) {
        vertx.deployVerticle(new EmptyConfigurableVerticle(), ctx.succeeding(id -> {
            assertConsoleContainsPattern("Starting");
            assertConsoleContainsPattern("Started");

            vertx.undeploy(id, ctx.succeeding(zoid -> {
                assertConsoleContainsPattern("Shutdown");
                ctx.completeNow();
            }));
        }));
    }


    @Test
    @DisplayName("Configurable verticle log levels")
    void configurableVerticleShouldLogAllLevels(Vertx vertx, VertxTestContext ctx) {
        EmptyConfigurableVerticle verticle = new EmptyConfigurableVerticle();
        verticle.trace("a message");
        assertConsoleContainsPattern("TRACE.*a message");
        verticle.trace("a message with throwable", new IllegalArgumentException("the throwable"));
        assertConsoleContainsPattern("TRACE.*a message with throwable");

        verticle.debug("a message");
        assertConsoleContainsPattern("DEBUG.*a message");
        verticle.debug("a message with throwable", new IllegalArgumentException("the throwable"));
        assertConsoleContainsPattern("DEBUG.*a message with throwable");

        verticle.info("a message");
        assertConsoleContainsPattern("INFO.*a message");
        verticle.info("a message with throwable", new IllegalArgumentException("the throwable"));
        assertConsoleContainsPattern("INFO.*a message with throwable");

        verticle.warn("a message");
        assertConsoleContainsPattern("WARN.*a message");
        verticle.warn("a message with throwable", new IllegalArgumentException("the throwable"));
        assertConsoleContainsPattern("WARN.*a message with throwable");

        verticle.error("a message");
        assertConsoleContainsPattern("ERROR.*a message");
        verticle.error("a message with throwable", new IllegalArgumentException("the throwable"));
        assertConsoleContainsPattern("ERROR.*a message with throwable");

        ctx.completeNow();
    }

    final class EmptyConfigurableVerticle extends ConfigurableVerticle<Options> {

        @Override
        protected Completable startVerticle() {
            return Completable.complete();
        }

        @Override
        protected Logger delegate() {
            return null;
        }

        @Override
        public Options readConfiguration(JsonObject config) {
            return JsonHelpers::obj;
        }

        @Override
        public Completable configure(Options config) {
            return Completable.complete();
        }

        @Override
        public Completable shutdown() {
            return Completable.complete();
        }

        @Override
        public ValidationResult validate(JsonObject config) {
            return ValidationResult.valid();
        }
    }
}