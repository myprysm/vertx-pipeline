package fr.myprysm.pipeline.pipeline.impl;

import com.google.common.collect.ImmutableSet;
import fr.myprysm.pipeline.VertxTest;
import fr.myprysm.pipeline.pipeline.DeployChannelActions;
import fr.myprysm.pipeline.pipeline.PipelineDeployment;
import fr.myprysm.pipeline.pipeline.PipelineOptions;
import fr.myprysm.pipeline.reactivex.pipeline.PipelineService;
import fr.myprysm.pipeline.util.ClasspathHelpers;
import fr.myprysm.pipeline.util.Signal;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import io.reactivex.Completable;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.serviceproxy.ServiceException;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("Duplicates")
class PipelineServiceImplTest implements VertxTest {

    @BeforeAll
    static void loadClasses(Vertx vertx, VertxTestContext ctx) {
        vertx.<ScanResult>executeBlocking(future -> future.complete(ClasspathHelpers.getScan()), ctx.succeeding(scan -> ctx.completeNow()));
    }

    private PipelineServiceImpl service1;
    private PipelineServiceImpl service2;
    private PipelineService rxService1;
    private PipelineService rxService2;
    private io.vertx.reactivex.core.Vertx rxVertx;

    @BeforeEach
    void setUp(Vertx vertx, VertxTestContext ctx) {
        rxVertx = new io.vertx.reactivex.core.Vertx(new FakeVertxCluster(vertx));
        service1 = new PipelineServiceImpl(rxVertx, "service-1");
        service2 = new PipelineServiceImpl(rxVertx, "service-2");
        rxService1 = new PipelineService(service1);
        rxService2 = new PipelineService(service2);

        Completable.concatArray(service1.configure(), service2.configure()).subscribe();
        ctx.completeNow();
    }

    @Nested
    class RegistrationsTest {
        @Test
        @DisplayName("It should see 2 nodes in the cluster")
        void itShouldSeeTwoNodesInTheCluster() {
            rxService1.rxGetNodes()
                    .test()
                    .assertValue(ImmutableSet.of("service-1", "service-2"));

            rxService2.rxGetNodes()
                    .test()
                    .assertValue(ImmutableSet.of("service-1", "service-2"));
        }

        @Test
        @DisplayName("It should see new nodes in the cluster")
        void itShouldSeeNewNodesInTheCluster() {
            PipelineServiceImpl service3 = new PipelineServiceImpl(rxVertx, "service-3");
            service3.configure()
                    .andThen(rxService1.rxGetNodes())
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertOf(test -> {
                        List<Set<String>> values = test.values();
                        assertThat(values).isNotNull();
                        assertThat(values.size()).isEqualTo(1);
                        Set<String> registrations = values.get(0);
                        assertThat(registrations).contains("service-1", "service-2", "service-3");
                    });

            service3.close()
                    .andThen(rxService1.rxGetNodes())
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertOf(test2 -> {
                        List<Set<String>> values2 = test2.values();
                        assertThat(values2).isNotNull();
                        assertThat(values2.size()).isEqualTo(1);
                        Set<String> registrations2 = values2.get(0);
                        assertThat(registrations2).contains("service-1", "service-2");
                    });
        }

        @Test
        @DisplayName("It should unregister from cluster")
        void itShouldUnregisterFromCluster() {
            PipelineServiceImpl service3 = new PipelineServiceImpl(rxVertx, "service-3");
            service3.configure()
                    .andThen(Completable.defer(service3::close))
                    .andThen(rxService1.rxGetNodes())
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertOf(test -> {
                        List<Set<String>> values = test.values();
                        assertThat(values).isNotNull();
                        assertThat(values.size()).isEqualTo(1);
                        Set<String> registrations = values.get(0);
                        assertThat(registrations).contains("service-1", "service-2");
                    });
        }
    }

    @Nested
    class PipelineStartTest {
        @Test
        @DisplayName("It should have a deployment")
        void itShouldHaveDeployment() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json")
                    .put("deployChannel", "service-1");
            rxService1.rxStartPipeline(new PipelineOptions(config), "service-1")
                    .flatMap(deployment -> rxService1.rxGetRunningPipelines())
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertOf(test -> {
                        List<Set<PipelineDeployment>> values = test.values();
                        assertThat(values).isNotNull();
                        assertThat(values.size()).isEqualTo(1);
                        Set<PipelineDeployment> deployments = values.get(0);
                        assertThat(deployments.size()).isEqualTo(1);
                    });
        }

        @Test
        @DisplayName("It should get a pipeline option from its name")
        void itShouldGetAPipelineOptionFromItsName() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json")
                    .put("deployChannel", "service-1");
            rxService1.rxStartPipeline(new PipelineOptions(config), "service-1")
                    .map(deployment -> new PipelineDeployment().setName(deployment.getName()))
                    .flatMap(rxService1::rxGetPipelineDescription)
                    .map(PipelineOptions::toJson)
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertOf(test -> {
                        List<JsonObject> values = test.values();
                        assertThat(values).isNotNull();
                        assertThat(values.size()).isEqualTo(1);
                        JsonObject remoteConfig = values.get(0);
                        assertThat(remoteConfig).isEqualTo(config);
                    });
        }

        @Test
        @DisplayName("It should start a pipeline from the same service")
        void itShouldStartPipelineFromTheSameService() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json")
                    .put("deployChannel", "service-1");
            rxService1.rxStartPipeline(new PipelineOptions(config), "service-1")
                    .flatMap(rxService1::rxGetPipelineDescription)
                    .map(PipelineOptions::toJson)
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertOf(test -> {
                        List<JsonObject> values = test.values();
                        assertThat(values).isNotNull();
                        assertThat(values.size()).isEqualTo(1);
                        JsonObject remoteConfig = values.get(0);
                        assertThat(remoteConfig).isEqualTo(config);
                    });
        }

        @Test
        @DisplayName("It should start a pipeline from a remote service")
        void itShouldStartPipelineFromARemoteService() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json")
                    .put("deployChannel", "service-1");
            rxService1.rxStartPipeline(new PipelineOptions(config), "service-1")
                    .flatMap(rxService2::rxGetPipelineDescription)
                    .map(PipelineOptions::toJson)
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertOf(test -> {
                        List<JsonObject> values = test.values();
                        assertThat(values).isNotNull();
                        assertThat(values.size()).isEqualTo(1);
                        JsonObject remoteConfig = values.get(0);
                        assertThat(remoteConfig).isEqualTo(config);
                    });
        }


        @Test
        @DisplayName("It should start a pipeline on a remote service")
        void itShouldStartPipelineOnARemoteService() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json")
                    .put("deployChannel", "service-2");

            rxService1.rxStartPipeline(new PipelineOptions(config), "service-2")
                    .flatMap(rxService1::rxGetPipelineDescription)
                    .map(PipelineOptions::toJson)
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertOf(test -> {
                        List<JsonObject> values = test.values();
                        assertThat(values).isNotNull();
                        assertThat(values.size()).isEqualTo(1);
                        JsonObject remoteConfig = values.get(0);
                        assertThat(remoteConfig).isEqualTo(config);
                    });
        }

        @Test
        @DisplayName("It should start a pipeline on local service when possible and no node provided")
        void itShouldStartPipelineOnLocalWhenPossible() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json")
                    .put("deployChannel", "service-1");

            rxService1.rxStartPipeline(new PipelineOptions(config), null)
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertOf(test -> {
                        List<PipelineDeployment> values = test.values();
                        assertThat(values).isNotNull();
                        assertThat(values.size()).isEqualTo(1);
                        PipelineDeployment deployment = values.get(0);
                        assertThat(deployment.getNode()).isEqualTo("service-1");
                    });
        }

        @Test
        @DisplayName("It should start a pipeline on local service whenever possible and no node provided")
        void itShouldStartPipelineOnRemoteWhenPossible(Vertx vertx) {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline-fake-pump.json")
                    .put("deployChannel", "service-1");

            // small trick to simulate the fact that a class does not exist in service 1 classpath while it exists in service 2.
            // We intercept the message that requests to start a pipeline on a remote service
            // and we replace the "FakeEventBusPump" with the real "EventBusPump".
            vertx.eventBus().addInterceptor(ctx -> {
                if (ctx.message().address().equals("service-2") && !ctx.message().headers().contains("replaced")) {
                    String body = ((String) ctx.message().body()).replaceAll("FakeEventBusPump", "EventBusPump");
                    MultiMap headers = ctx.message().headers();
                    headers.add("replaced", "replaced");
                    vertx.eventBus().send(ctx.message().address(), body, new DeliveryOptions().setHeaders(headers), ar -> {
                        if (ar.succeeded()) {
                            ctx.message().reply(ar.result().body());
                        } else {
                            ctx.message().reply(ar.cause());
                        }
                    });
                } else {
                    ctx.next();
                }
            });

            rxService1.rxStartPipeline(new PipelineOptions(config), null)
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertOf(test -> {
                        List<PipelineDeployment> values = test.values();
                        assertThat(values).isNotNull();
                        assertThat(values.size()).isEqualTo(1);
                        PipelineDeployment deployment = values.get(0);
                        assertThat(deployment.getNode()).isEqualTo("service-2");
                    });
        }

    }

    @Nested
    class PipelineStopTest {
        @Test
        @DisplayName("It should stop a pipeline from its name")
        void itShouldStopPipelineFromItsName() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json")
                    .put("deployChannel", "service-1");
            rxService1.rxStartPipeline(new PipelineOptions(config), "service-1")
                    .map(deployment -> new PipelineDeployment().setName(deployment.getName()))
                    .flatMapCompletable(rxService1::rxStopPipeline)
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertComplete();
        }

        @Test
        @DisplayName("It should stop a pipeline from the same service")
        void itShouldStopPipelineFromTheSameService() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json")
                    .put("deployChannel", "service-1");
            rxService1.rxStartPipeline(new PipelineOptions(config), "service-1")
                    .flatMapCompletable(rxService1::rxStopPipeline)
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertComplete();
        }

        @Test
        @DisplayName("It should stop a pipeline from a remote service")
        void itShouldStopPipelineFromRemoteService() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json")
                    .put("deployChannel", "service-1");
            rxService1.rxStartPipeline(new PipelineOptions(config), "service-1")
                    .flatMapCompletable(rxService2::rxStopPipeline)
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertComplete();
        }


        @Test
        @DisplayName("It should stop a pipeline on a remote service")
        void itShouldStopAPipelineOnARemoteService() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json")
                    .put("deployChannel", "service-2");

            rxService1.rxStartPipeline(new PipelineOptions(config), "service-2")
                    .flatMapCompletable(rxService1::rxStopPipeline)
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertComplete();
        }

        @Test
        @DisplayName("It should stop a pipeline from the deploy channel")
        void itShouldStopAPipelineFromDeployChannel() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json")
                    .put("deployChannel", "service-1");

            rxService1.rxStartPipeline(new PipelineOptions(config), "service-1")
                    .flatMap(deployment -> {
                        DeliveryOptions options = new DeliveryOptions().addHeader("action", DeployChannelActions.UNDEPLOY.name());
                        return rxVertx.eventBus().<String>rxSend(deployment.getNode(), deployment.getName(), options);
                    })
                    .toCompletable()
                    .andThen(rxService2.rxGetRunningPipelines())
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertOf(test -> {
                        List<Set<PipelineDeployment>> values = test.values();
                        assertThat(values).isNotNull();
                        assertThat(values.size()).isEqualTo(1);
                        Set<PipelineDeployment> deployments = values.get(0);
                        assertThat(deployments.size()).isEqualTo(0);
                    });
        }

        @Test
        @DisplayName("It should stop a pipeline when terminated")
        void itShouldStopAPipelineWhenTerminated() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json")
                    .put("deployChannel", "service-1");

            rxService1.rxStartPipeline(new PipelineOptions(config), "service-1")
                    .flatMap(deployment -> rxVertx.eventBus().<String>rxSend(deployment.getControlChannel(), Signal.TERMINATE.toString()))
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertOf(test -> {
                        List<Message<String>> values = test.values();
                        assertThat(values).isNotNull();
                        assertThat(values.size()).isEqualTo(1);
                        Message<String> message = values.get(0);
                        assertThat(message.body()).isEqualTo("acknowledged");
                        rxService2.rxGetRunningPipelines()
                                .test()
                                .awaitDone(5, TimeUnit.SECONDS)
                                .assertOf(test2 -> {
                                    List<Set<PipelineDeployment>> values2 = test2.values();
                                    assertThat(values2).isNotNull();
                                    assertThat(values2.size()).isEqualTo(1);
                                    Set<PipelineDeployment> deployments = values2.get(0);
                                    assertThat(deployments.size()).isEqualTo(0);
                                });
                    });
        }

    }

    @Nested
    class PipelineErrorsTest {

        @Test
        @DisplayName("It should not provide config without pipeline name")
        void itShouldNotProvideConfigWithoutPipelineName() {
            rxService1.rxGetPipelineDescription(new PipelineDeployment())
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertError(error -> {
                        assertThat(error).isInstanceOf(ServiceException.class);
                        assertThat(((ServiceException) error).failureCode()).isEqualTo(PipelineServiceImpl.INVALID_DEPLOYMENT);
                        assertThat(error.getMessage()).isEqualTo("No name found in deployment.");
                        return true;
                    });
        }

        @Test
        @DisplayName("It should not provide config without existing pipeline name")
        void itShouldNotProvideConfigWithoutExistingPipelineName() {
            rxService1.rxGetPipelineDescription(new PipelineDeployment().setName("foobar"))
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertError(error -> {
                        assertThat(error).isInstanceOf(ServiceException.class);
                        assertThat(((ServiceException) error).failureCode()).isEqualTo(PipelineServiceImpl.PIPELINE_NOT_FOUND);
                        assertThat(error.getMessage()).isEqualTo("Unable to find pipeline foobar");
                        return true;
                    });
        }

        @Test
        @DisplayName("It should not start a pipeline on an unexisting node")
        void itShouldNotStartPipelineOnAnUnexistingNode() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json");

            rxService1.rxStartPipeline(new PipelineOptions(config).setDeployChannel("service-1"), "foobar")
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertError(error -> {
                        assertThat(error).isInstanceOf(ServiceException.class);
                        assertThat(((ServiceException) error).failureCode()).isEqualTo(PipelineServiceImpl.NO_CLUSTER_MATCH);
                        assertThat(error.getMessage()).isEqualTo("Registration 'foobar' not found.");
                        return true;
                    });
        }

        @Test
        @DisplayName("It should not start a pipeline with invalid config")
        void itShouldNotStartPipelineWithInvalidConfig() {
            JsonObject config = objectFromFile("pipeline-service/invalid-config-pipeline.json");

            rxService1.rxStartPipeline(new PipelineOptions(config).setDeployChannel("service-1"), "service-1")
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertError(error -> {
                        assertThat(error).isInstanceOf(ServiceException.class);
                        assertThat(((ServiceException) error).failureCode()).isEqualTo(PipelineServiceImpl.DEPLOYMENT_ERROR);
                        assertThat(error.getMessage()).isEqualTo("An error occured while deploying pipeline 'event-bus-pipeline': JOLT Chainr passed an empty JSON array.");
                        return true;
                    });
        }

        @Test
        @DisplayName("It should not start a pipeline with the same name")
        void itShouldNotStartPipelineWithTheSameName() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline.json");

            rxService1.rxStartPipeline(new PipelineOptions(config).setDeployChannel("service-1"), "service-1").toCompletable()
                    .andThen(rxService2.rxStartPipeline(new PipelineOptions(config).setDeployChannel("service-2"), "service-2"))
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertError(error -> {
                        assertThat(error).isInstanceOf(ServiceException.class);
                        assertThat(((ServiceException) error).failureCode()).isEqualTo(PipelineServiceImpl.DUPLICATE_NAME);
                        assertThat(error.getMessage()).isEqualTo("A pipeline with the name 'event-bus-pipeline' is already deployed.");
                        return true;
                    });
        }

        @Test
        @DisplayName("It should not start a pipeline on local service when possible and no node provided")
        void itShouldNotStartPipelineWhenAComponentIsNotAvailableOnCluster() {
            JsonObject config = objectFromFile("pipeline-service/event-bus-pipeline-fake-pump.json")
                    .put("deployChannel", "service-1");

            rxService1.rxStartPipeline(new PipelineOptions(config), null)
                    .test()
                    .awaitDone(5, TimeUnit.SECONDS)
                    .assertError(error -> {
                        assertThat(error).isInstanceOf(ServiceException.class);
                        assertThat(((ServiceException) error).failureCode()).isEqualTo(PipelineServiceImpl.NO_CLUSTER_MATCH);
                        assertThat(error.getMessage()).isEqualTo("Unable to find a matching node to deploy the pipeline...");
                        return true;
                    });
        }

        @Test
        @DisplayName("It should fail to stop without a pipeline name")
        void itShouldFailToStopWithoutPipelineName() {
            rxService1.rxStopPipeline(new PipelineDeployment())
                    .test()
                    .assertError(error -> {
                        assertThat(error).isInstanceOf(ServiceException.class);
                        assertThat(((ServiceException) error).failureCode()).isEqualTo(PipelineServiceImpl.INVALID_DEPLOYMENT);
                        assertThat(error.getMessage()).isEqualTo("No name found in deployment.");
                        return true;
                    });
        }


        @Test
        @DisplayName("It should fail to stop an unexisting pipeline")
        void itShouldFailToStopAnUnexistingPipelineName() {
            rxService1.rxStopPipeline(new PipelineDeployment().setName("foobar"))
                    .test()
                    .assertError(error -> {
                        assertThat(error).isInstanceOf(ServiceException.class);
                        assertThat(((ServiceException) error).failureCode()).isEqualTo(PipelineServiceImpl.PIPELINE_NOT_FOUND);
                        assertThat(error.getMessage()).isEqualTo("Unable to find pipeline foobar");
                        return true;
                    });
        }
    }

    @AfterEach
    void teardown() {
        Completable.concatArray(service1.close(false), service2.close(false)).subscribe();
    }

}