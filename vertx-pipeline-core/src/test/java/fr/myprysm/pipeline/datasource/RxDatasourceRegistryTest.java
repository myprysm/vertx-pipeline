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

package fr.myprysm.pipeline.datasource;

import fr.myprysm.pipeline.QuickAssert;
import fr.myprysm.pipeline.QuickRxAssert;
import fr.myprysm.pipeline.VertxTest;
import fr.myprysm.pipeline.reactivex.datasource.DatasourceRegistry;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.serviceproxy.ServiceBinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("(Rx)DatasourceRegistry implementation tests")
class RxDatasourceRegistryTest implements VertxTest, QuickAssert, QuickRxAssert {

    private static final DatasourceRegistration SIMPLE_LOCAL_REGISTRATION = new DatasourceRegistration()
            .setAlias("alias")
            .setComponent("component")
            .setConfiguration("configuration")
            .setDeployment("deployment");
    private static final DatasourceRegistration SIMPLE_REMOTE_REGISTRATION = new DatasourceRegistration(SIMPLE_LOCAL_REGISTRATION)
            .setDeployment("remote-deployment");

    private static final DatasourceConfiguration SIMPLE_LOCAL_CONFIGURATION = new DatasourceConfiguration()
            .setDeployment("deployment")
            .setName("name")
            .setProperties(obj());

    private static final DatasourceConfiguration SIMPLE_CONFLICT_REMOTE_CONFIGURATION = new DatasourceConfiguration(SIMPLE_LOCAL_CONFIGURATION)
            .setDeployment("remote-deployment");

    private static final DatasourceConfiguration SIMPLE_REMOTE_CONFIGURATION = new DatasourceConfiguration(SIMPLE_CONFLICT_REMOTE_CONFIGURATION)
            .setName("remote-name");

    private DatasourceRegistry registry;
    private DatasourceRegistry proxy;

    @BeforeEach
    void setupRegistry(Vertx vertx, VertxTestContext ctx) {
        registry = fr.myprysm.pipeline.datasource.DatasourceRegistry.createRx(vertx, "deployment", ctx.succeeding(registry -> {
            new ServiceBinder(vertx)
                    .setAddress(fr.myprysm.pipeline.datasource.DatasourceRegistry.ADDRESS)
                    .register(fr.myprysm.pipeline.datasource.DatasourceRegistry.class, registry);

            proxy = fr.myprysm.pipeline.datasource.DatasourceRegistry.createRxProxy(vertx, fr.myprysm.pipeline.datasource.DatasourceRegistry.ADDRESS);
            ctx.completeNow();
        }));
    }

    @Nested
    @DisplayName("Registration tests")
    class RegistrationTest {
        @Test
        @DisplayName("Registry should handle a datasource registration")
        void registryShouldHandleDatasourceRegistration(VertxTestContext ctx) {
            registry.registerComponent(SIMPLE_LOCAL_REGISTRATION, assertEquals(true, ctx.checkpoint(), ctx));
        }

        @Test
        @DisplayName("Registry should register the same datasource once")
        void registryShouldRegisterSameDatasourceOnce(VertxTestContext ctx) {
            registry.registerComponent(SIMPLE_LOCAL_REGISTRATION, ctx.succeeding(added -> {
                Checkpoint cp = ctx.checkpoint(2);
                ctx.verify(() -> assertThat(added).isTrue());

                registry.registerComponent(SIMPLE_LOCAL_REGISTRATION, assertEquals(false, cp, ctx));
                registry.registerComponent(SIMPLE_LOCAL_REGISTRATION, assertEquals(false, cp, ctx));

            }));
        }

        @Test
        @DisplayName("Registry should register datasource from different deployments")
        void registryShouldRegisterDatasourceFromDifferentDeployments(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(2);
            registry.registerComponent(SIMPLE_LOCAL_REGISTRATION, assertEquals(true, cp, ctx));
            registry.registerComponent(SIMPLE_REMOTE_REGISTRATION, assertEquals(true, cp, ctx));
        }

        @Test
        @DisplayName("Registry should unregister component")
        void registryShouldUnregisterComponent(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(4);

            registry.registerComponent(SIMPLE_LOCAL_REGISTRATION, assertEquals(true, cp, ctx));
            registry.unregisterComponent(SIMPLE_LOCAL_REGISTRATION, assertEquals(true, cp, ctx));
            registry.unregisterComponent(SIMPLE_LOCAL_REGISTRATION, assertEquals(false, cp, ctx));
            registry.unregisterComponent(SIMPLE_REMOTE_REGISTRATION,
                    assertThrows(DatasourceRegistryException.class, "Could not find deployment 'remote-deployment'", cp, ctx));
        }

        @Test
        @DisplayName("Registry should return two registrations for Alias, Component and Configuration, One for each deployment")
        void registryShouldReturnTwoRegistrationsForAliasComponentAndConfiguration(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(5);

            registry.registerComponent(SIMPLE_LOCAL_REGISTRATION, ctx.succeeding());
            registry.registerComponent(SIMPLE_REMOTE_REGISTRATION, ctx.succeeding());
            registry.registrationsForAlias("alias", assertSize(2, cp, ctx));
            registry.registrationsForComponent("component", assertSize(2, cp, ctx));
            registry.registrationsForConfiguration("configuration", assertSize(2, cp, ctx));
            registry.registrationsForDeployment("deployment", assertSize(1, cp, ctx));
            registry.registrationsForDeployment("remote-deployment", assertSize(1, cp, ctx));
        }

        @Test
        @DisplayName("Registry should return one registration for Alias, Component and Configuration, only one deployment")
        void registryShouldReturnOneRegistrationForAliasComponentAndConfiguration(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(5);

            registry.registerComponent(SIMPLE_LOCAL_REGISTRATION, ctx.succeeding());
            registry.registerComponent(SIMPLE_REMOTE_REGISTRATION, ctx.succeeding());
            ((DatasourceRegistryImpl) registry.getDelegate()).close(ctx.succeeding());

            registry.registrationsForAlias("alias", assertSize(1, cp, ctx));
            registry.registrationsForComponent("component", assertSize(1, cp, ctx));
            registry.registrationsForConfiguration("configuration", assertSize(1, cp, ctx));
            registry.registrationsForDeployment("remote-deployment", assertSize(1, cp, ctx));
            registry.registrationsForDeployment("deployment", assertThrows(DatasourceRegistryException.class, "Could not find deployment 'deployment'", cp, ctx));
        }

    }

    @Nested
    @DisplayName("Configuration tests")
    class ConfigurationTest {

        @Test
        @DisplayName("Registry should add configuration")
        void registryShouldAddConfiguration(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(2);
            registry.storeConfiguration(SIMPLE_LOCAL_CONFIGURATION, ctx.succeeding(z -> cp.flag()));
            registry.storeConfiguration(SIMPLE_REMOTE_CONFIGURATION, ctx.succeeding(z -> cp.flag()));
        }

        @Test
        @DisplayName("Registry should not add the same configuration twice")
        void registryShouldNotAddSameConfigurationTwice(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(2);
            registry.storeConfiguration(SIMPLE_LOCAL_CONFIGURATION, ctx.succeeding(z -> cp.flag()));
            registry.storeConfiguration(SIMPLE_LOCAL_CONFIGURATION, ctx.failing(z -> cp.flag()));
        }

        @Test
        @DisplayName("Registry should not another configuration with the same name")
        void registryShouldNotAddAnotherConfigurationWithTheSameName(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(2);
            registry.storeConfiguration(SIMPLE_LOCAL_CONFIGURATION, ctx.succeeding(z -> cp.flag()));
            registry.storeConfiguration(SIMPLE_CONFLICT_REMOTE_CONFIGURATION, ctx.failing(z -> cp.flag()));
        }

        @Test
        @DisplayName("Registry should provide configuration")
        void registryShouldProvideConfiguration(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(2);

            registry.storeConfiguration(SIMPLE_LOCAL_CONFIGURATION, ctx.succeeding());
            registry.storeConfiguration(SIMPLE_REMOTE_CONFIGURATION, ctx.succeeding());

            registry.getConfiguration("name", assertEquals(SIMPLE_LOCAL_CONFIGURATION, cp, ctx));
            registry.getConfiguration("remote-name", assertEquals(SIMPLE_REMOTE_CONFIGURATION, cp, ctx));
        }

        @Test
        @DisplayName("Registry should unregister configuration on close")
        void registryShouldUnregisterConfigurationOnClose(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(2);

            registry.storeConfiguration(SIMPLE_LOCAL_CONFIGURATION, ctx.succeeding());
            registry.storeConfiguration(SIMPLE_REMOTE_CONFIGURATION, ctx.succeeding());
            ((DatasourceRegistryImpl) registry.getDelegate()).close(ctx.succeeding());

            registry.getConfiguration("name", assertThrows(DatasourceRegistryException.class, "Could not find configuration for name 'name'", cp, ctx));
            registry.getConfiguration("remote-name", assertEquals(SIMPLE_REMOTE_CONFIGURATION, cp, ctx));
        }

    }

    @Nested
    @DisplayName("Rx Registration tests")
    class RxRegistrationTest {
        @Test
        @DisplayName("Registry should handle a datasource registration")
        void registryShouldHandleDatasourceRegistration(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint();
            registry.rxRegisterComponent(SIMPLE_LOCAL_REGISTRATION).test()
                    .assertOf(assertEquals(true, cp));
        }

        @Test
        @DisplayName("Registry should register the same datasource once")
        void registryShouldRegisterSameDatasourceOnce(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(2);
            registry.rxRegisterComponent(SIMPLE_LOCAL_REGISTRATION)
                    .test()
                    .assertOf(check -> {
                        check.assertValue(true);
                        registry.rxRegisterComponent(SIMPLE_LOCAL_REGISTRATION).test().assertOf(assertEquals(false, cp));
                        registry.rxRegisterComponent(SIMPLE_LOCAL_REGISTRATION).test().assertOf(assertEquals(false, cp));
                    });
        }

        @Test
        @DisplayName("Registry should register datasource from different deployments")
        void registryShouldRegisterDatasourceFromDifferentDeployments(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(2);
            registry.rxRegisterComponent(SIMPLE_LOCAL_REGISTRATION).test().assertOf(assertEquals(true, cp));
            registry.rxRegisterComponent(SIMPLE_REMOTE_REGISTRATION).test().assertOf(assertEquals(true, cp));
        }

        @Test
        @DisplayName("Registry should return two registrations for Alias, Component and Configuration, One for each deployment")
        void registryShouldReturnTwoRegistrationsForAliasComponentAndConfiguration(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(5);
            registry.rxRegisterComponent(SIMPLE_LOCAL_REGISTRATION).test().assertValue(true);
            registry.rxRegisterComponent(SIMPLE_REMOTE_REGISTRATION).test().assertValue(true);

            registry.rxRegistrationsForAlias("alias").test().assertOf(assertSize(2, cp));
            registry.rxRegistrationsForComponent("component").test().assertOf(assertSize(2, cp));
            registry.rxRegistrationsForConfiguration("configuration").test().assertOf(assertSize(2, cp));
            registry.rxRegistrationsForDeployment("deployment").test().assertOf(assertSize(1, cp));
            registry.rxRegistrationsForDeployment("remote-deployment").test().assertOf(assertSize(1, cp));
        }

        @Test
        @DisplayName("Registry should return one registration for Alias, Component and Configuration, only one deployment")
        void registryShouldReturnOneRegistrationForAliasComponentAndConfiguration(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(5);

            registry.rxRegisterComponent(SIMPLE_LOCAL_REGISTRATION).test().assertValue(true);
            registry.rxRegisterComponent(SIMPLE_REMOTE_REGISTRATION).test().assertValue(true);
            ((DatasourceRegistryImpl) registry.getDelegate()).close(ctx.succeeding());

            registry.rxRegistrationsForAlias("alias").test().assertOf(assertSize(1, cp));
            registry.rxRegistrationsForComponent("component").test().assertOf(assertSize(1, cp));
            registry.rxRegistrationsForConfiguration("configuration").test().assertOf(assertSize(1, cp));
            registry.rxRegistrationsForDeployment("remote-deployment").test().assertOf(assertSize(1, cp));
            registry.rxRegistrationsForDeployment("deployment").test()
                    .assertOf(check -> {
                        check.assertError(DatasourceRegistryException.class).assertErrorMessage("Could not find deployment 'deployment'");
                        cp.flag();
                    });
        }

        @Test
        @DisplayName("Registry should unregister component")
        void registryShouldUnregisterComponent(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(4);

            registry.rxRegisterComponent(SIMPLE_LOCAL_REGISTRATION).test().assertOf(assertEquals(true, cp));
            registry.rxUnregisterComponent(SIMPLE_LOCAL_REGISTRATION).test().assertOf(assertEquals(true, cp));
            registry.rxUnregisterComponent(SIMPLE_LOCAL_REGISTRATION).test().assertOf(assertEquals(false, cp));
            registry.rxUnregisterComponent(SIMPLE_REMOTE_REGISTRATION).test()
                    .assertOf(assertError(DatasourceRegistryException.class, "Could not find deployment 'remote-deployment'", cp));
        }
    }

    @Nested
    @DisplayName("Rx Configuration tests")
    class RxConfigurationTest {

        @Test
        @DisplayName("Registry should add configuration")
        void registryShouldAddConfiguration(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(2);
            registry.rxStoreConfiguration(SIMPLE_LOCAL_CONFIGURATION).test().assertOf(assertComplete(cp));
            registry.rxStoreConfiguration(SIMPLE_REMOTE_CONFIGURATION).test().assertOf(assertComplete(cp));
        }


        @Test
        @DisplayName("Registry should not add the same configuration twice")
        void registryShouldNotAddSameConfigurationTwice(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(2);
            registry.rxStoreConfiguration(SIMPLE_LOCAL_CONFIGURATION).test().assertOf(assertComplete(cp));
            registry.rxStoreConfiguration(SIMPLE_LOCAL_CONFIGURATION).test().assertOf(assertError(cp));
        }

        @Test
        @DisplayName("Registry should not another configuration with the same name")
        void registryShouldNotAddAnotherConfigurationWithTheSameName(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(2);
            registry.rxStoreConfiguration(SIMPLE_LOCAL_CONFIGURATION).test().assertOf(assertComplete(cp));
            registry.rxStoreConfiguration(SIMPLE_CONFLICT_REMOTE_CONFIGURATION).test().assertOf(assertError(cp));
        }

        @Test
        @DisplayName("Registry should provide configuration")
        void registryShouldProvideConfiguration(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(2);

            registry.rxStoreConfiguration(SIMPLE_LOCAL_CONFIGURATION).test().assertOf(assertComplete(cp));
            registry.rxStoreConfiguration(SIMPLE_REMOTE_CONFIGURATION).test().assertOf(assertComplete(cp));

            registry.rxGetConfiguration("name").test().assertOf(assertEquals(SIMPLE_LOCAL_CONFIGURATION, cp));
            registry.rxGetConfiguration("remote-name").test().assertOf(assertEquals(SIMPLE_REMOTE_CONFIGURATION, cp));
        }

        @Test
        @DisplayName("Registry should unregister configuration on close")
        void registryShouldUnregisterConfigurationOnClose(VertxTestContext ctx) {
            Checkpoint cp = ctx.checkpoint(2);

            registry.rxStoreConfiguration(SIMPLE_LOCAL_CONFIGURATION).test().assertOf(assertComplete(cp));
            registry.rxStoreConfiguration(SIMPLE_REMOTE_CONFIGURATION).test().assertOf(assertComplete(cp));
            ((DatasourceRegistryImpl) registry.getDelegate()).close(ctx.succeeding());

            registry.rxGetConfiguration("name").test().assertOf(assertError(DatasourceRegistryException.class, "Could not find configuration for name 'name'", cp));
            registry.rxGetConfiguration("remote-name").test().assertOf(assertEquals(SIMPLE_REMOTE_CONFIGURATION, cp));
        }

    }

    @Test
    @DisplayName("Registry should always close successfully")
    void registryShouldUnregisterConfigurationOnClose(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint(2);

        ((DatasourceRegistryImpl) registry.getDelegate()).close(assertSuccess(cp, ctx));
        ((DatasourceRegistryImpl) registry.getDelegate()).close(assertSuccess(cp, ctx));
    }

    @Test
    @DisplayName("Rx Registry coverage")
    void rxRegistryCoverage(VertxTestContext ctx) {
        assertThat(registry).isNotEqualTo(proxy);
        assertThat(registry).isNotEqualTo(null);
        assertThat(registry).isNotEqualTo(registry.getDelegate());
        assertThat(registry.hashCode()).isNotEqualTo(proxy.hashCode());
        assertThat(registry.toString()).isNotEqualTo(proxy.toString());

        fr.myprysm.pipeline.datasource.DatasourceRegistry unwrap = DatasourceRegistry.__TYPE_ARG.unwrap(registry);
        DatasourceRegistry wrap = DatasourceRegistry.__TYPE_ARG.wrap(registry.getDelegate());
        assertThat(unwrap).isEqualTo(registry.getDelegate());
        assertThat(registry).isEqualTo(wrap);
        assertThat(registry).isEqualTo(DatasourceRegistry.newInstance(registry.getDelegate()));

        ctx.completeNow();
    }
}