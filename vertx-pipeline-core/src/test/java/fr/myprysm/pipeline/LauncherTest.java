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

import io.reactivex.plugins.RxJavaPlugins;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LauncherTest implements VertxTest {

    public static final String LAUNCHER_BIND = "fr\\.myprysm\\.pipeline\\.Launcher\\$\\$Lambda.*";

    @Test
    @DisplayName("Launcher should bind Rx Schedulers on Vert.x")
    void launcherShouldBindRxSchedulersOnVertx(Vertx vertx, VertxTestContext ctx) {
        Launcher launcher = new Launcher();
        launcher.afterStartingVertx(vertx);

        ctx.verify(() -> {
            assertThat(RxJavaPlugins.getComputationSchedulerHandler().getClass().getName()).matches(LAUNCHER_BIND);
            assertThat(RxJavaPlugins.getIoSchedulerHandler().getClass().getName()).matches(LAUNCHER_BIND);
            assertThat(RxJavaPlugins.getNewThreadSchedulerHandler().getClass().getName()).matches(LAUNCHER_BIND);
            RxJavaPlugins.reset();
            ctx.completeNow();
        });


    }
}