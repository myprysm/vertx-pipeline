package fr.myprysm.pipeline.pipeline.impl;

import fr.myprysm.pipeline.pipeline.*;
import fr.myprysm.pipeline.util.EnumUtils;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.CompletableHelper;
import io.vertx.reactivex.SingleHelper;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.core.shareddata.AsyncMap;
import io.vertx.serviceproxy.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Pipeline Service is the service dedicated to pipeline management.
 */
@Slf4j
public class PipelineServiceImpl implements PipelineService {


    private static int PIPELINE_NOT_FOUND = 404;
    private static int DUPLICATE_NAME = 405;
    private static int NO_CLUSTER_MATCH = 409;
    private static int INVALID_OPTIONS = 416;
    private static int INVALID_DEPLOYMENT = 417;
    private static int REMOTE_SERVICE_ERROR = 418;

    private static final String PIPELINES_MAP = "fr.myprysm.pipeline:pipelines";
    private static final String SERVICE_CHANNEL_ADDRESS = "fr.myprysm.pipeline:pipeline-service-channel";

    public static final String PIPELINE_VERTICLE = "fr.myprysm.pipeline.pipeline.PipelineVerticle";


    private final Vertx vertx;
    private final String deployChannel;
    private final boolean clustered;
    private MessageConsumer<String> deployChannelConsumer;
    private MessageConsumer<JsonObject> serviceChannelConsumer;
    /**
     * The deployments across the nodes.
     */
    private AsyncMap<PipelineDeployment, PipelineOptions> pipelines;
    private Set<String> registrations = new ConcurrentHashSet<>();

    public PipelineServiceImpl(Vertx vertx, String deployChannel) {
        this.vertx = vertx;
        this.deployChannel = deployChannel;
        clustered = vertx.isClustered();
    }

    //region Configuration
    public Completable configure() {
        return Completable.concatArray(
                preparePipelinesMap(),
                prepareDeployChannelConsumer(),
                prepareServiceChannelConsumer()
        )
                .andThen(Completable.defer(this::registerService));
    }


    private Completable preparePipelinesMap() {
        return vertx.sharedData()
                .<PipelineDeployment, PipelineOptions>rxGetAsyncMap(PIPELINES_MAP)
                .doOnSuccess(this::setPipelines)
                .toCompletable();
    }

    private void setPipelines(AsyncMap<PipelineDeployment, PipelineOptions> pipelines) {
        this.pipelines = pipelines;
    }

    private Completable registerService() {
        JsonObject data = new JsonObject().put("service", deployChannel);
        DeliveryOptions options = new DeliveryOptions().addHeader("action", ServiceChannelActions.REGISTER_SERVICE.name());
        vertx.eventBus().publish(SERVICE_CHANNEL_ADDRESS, data, options);
        return Completable.complete();
    }

    private Completable prepareDeployChannelConsumer() {
        deployChannelConsumer = vertx.eventBus().consumer(deployChannel, this::deployChannelActionDispatcher);
        return Completable.complete();
    }

    private Completable prepareServiceChannelConsumer() {
        serviceChannelConsumer = vertx.eventBus().consumer(SERVICE_CHANNEL_ADDRESS, this::serviceChannelActionDispatcher);
        return Completable.complete();
    }
    //endregion

    private void addRegistration(String registration) {
        if (!deployChannel.equals(registration)) {
            if (registrations.add(registration)) {
                log.info("[{}] registered [{}]", deployChannel, registration);
            } else {
                log.error("[{}] already registered [{}]", deployChannel, registration);
            }
        }
    }

    private void removeRegistration(String registration) {
        if (!deployChannel.equals(registration)) {
            if (registrations.remove(registration)) {
                log.info("[{}] unregistered [{}]", deployChannel, registration);
            } else {
                log.error("[{}] already unregistered [{}]", deployChannel, registration);
            }
        }
    }

    //region Service Channel
    private void serviceChannelActionDispatcher(Message<JsonObject> message) {
        ServiceChannelActions action = EnumUtils.fromString(message.headers().get("action"), ServiceChannelActions.class);
        if (action != null) {
            switch (action) {
                case REGISTER_SERVICE:
                    handleRegisterService(message.body().getString("service"));
                    break;
                case UNREGISTER_SERVICE:
                    removeRegistration(message.body().getString("service"));
            }
        }
    }


    /**
     * Responds to the registration by emitting the ACK. (so that the other service knows this one)
     *
     * @param service the pipeline service id
     */
    private void handleRegisterService(String service) {
        addRegistration(service);

        DeliveryOptions options = new DeliveryOptions().addHeader("action", DeployChannelActions.REGISTRATION_ACK.name());
        vertx.eventBus().send(service, deployChannel, options);
    }

    //endregion

    //region Deploy Channel
    private void deployChannelActionDispatcher(Message<String> message) {
        DeployChannelActions action = EnumUtils.fromString(message.headers().get("action"), DeployChannelActions.class);
        String body = message.body();
        if (action != null && body != null) {
            switch (action) {
                case UNDEPLOY:
                    handleUndeploy(message);
                    break;
                case DEPLOY:
                    handleDeploy(message);
                    break;
                case REGISTRATION_ACK:
                    addRegistration(body);
                    break;
            }
        }

    }

    private void handleDeploy(Message<String> message) {
        if (message.headers().get("from") != null) {
            log.info("Service [{}] requests to start a pipeline", message.headers().get("from"));
        }
        PipelineOptions options = new PipelineOptions(new JsonObject(message.body()));
        startPipelineInternal(options, false, ar -> {
            if (ar.succeeded()) {
                message.reply(ar.result().toJson().toString());
            } else {
                message.reply(ar.cause());
            }
        });
    }

    private void handleUndeploy(Message<String> pipeline) {
        getDeployments()
                .flatMapObservable(Observable::fromIterable)
                .filter(deployment -> deployment.getNode().equals(deployChannel) && deployment.getName().equals(pipeline.body()))
                .firstOrError()
                .flatMapCompletable(this::undeployLocalPipeline)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                            log.info("Undeployed [{}].", pipeline);
                            if (pipeline.replyAddress() != null) {
                                pipeline.reply("acknowledged");
                            }
                        },
                        throwable -> {
                            log.error("Error while undeploying pipeline.", throwable);
                            if (pipeline.replyAddress() != null) {
                                pipeline.reply(throwable instanceof ServiceException ? throwable : new ServiceException(-1, throwable.getMessage()));
                            }
                        }
                );
    }
    //endregion


    //region Service implementation
    @Override
    public void getRunningPipelines(Handler<AsyncResult<Set<PipelineDeployment>>> handler) {
        pipelinesDelegate().keys(handler);
    }

    @Override
    public void getPipelineDescription(PipelineDeployment deployment, Handler<AsyncResult<PipelineOptions>> handler) {
        getOptions(deployment).subscribe(SingleHelper.toObserver(handler));
    }

    @Override
    public void startPipeline(PipelineOptions options, Handler<AsyncResult<PipelineDeployment>> handler) {
        startPipelineInternal(options, true, handler);
    }

    /**
     * Starts the pipeline with the provided configuration.
     * <p>
     * In cluster mode, the flag <code>tryCluster</code> indicates whether the service should try to request other
     * registered services to start the pipeline.
     *
     * @param options    the pipeline options
     * @param tryCluster indicates whether the deployment should be tried on the cluster
     * @param handler    the result handler
     */
    private void startPipelineInternal(PipelineOptions options, boolean tryCluster, Handler<AsyncResult<PipelineDeployment>> handler) {
        ValidationResult validation = PipelineOptionsValidation.validate(options.toJson());
        if (validation.isValid()) {
            validateName(options.getName())
                    .andThen(startLocalPipeline(options))
                    .subscribe(SingleHelper.toObserver(handler));
        } else if (clustered && tryCluster) {
            tryStartOnCluster(options, registrations.iterator(), handler);
        } else {
            handler.handle(ServiceException.fail(INVALID_OPTIONS, "Options for pipeline " + options.getName() + " are invalid: " + validation.getReason().get(), options.toJson()));
        }
    }

    private void tryStartOnCluster(PipelineOptions options, Iterator<String> registrations, Handler<AsyncResult<PipelineDeployment>> handler) {
        if (!registrations.hasNext()) {
            handler.handle(ServiceException.fail(NO_CLUSTER_MATCH, "Unable to find a matching node to deploy the pipeline...", options.toJson()));
        }
        String next = registrations.next();
        actionDeploy(options, next, ar -> {
            if (ar.succeeded()) {
                handler.handle(ar);
            } else {
                log.error("Unable to start pipeline on node {}.", next);
                log.error("Reason: ", ar.cause());
                tryStartOnCluster(options, registrations, handler);
            }
        });
    }

    private void actionDeploy(PipelineOptions options, String remote, Handler<AsyncResult<PipelineDeployment>> handler) {
        DeliveryOptions deliveryOptions = new DeliveryOptions()
                .addHeader("action", DeployChannelActions.DEPLOY.name())
                .addHeader("from", deployChannel);

        vertx.eventBus().<String>rxSend(remote, options.toJson().toString(), deliveryOptions).subscribe(ar -> {
            try {
                JsonObject json = new JsonObject(ar.body());
                handler.handle(Future.succeededFuture(new PipelineDeployment(json)));
            } catch (Exception exc) {
                handler.handle(Future.failedFuture(exc));
            }
        });
    }

    /**
     * Starts the pipeline locally.
     * <p>
     * Generates a random control channel.
     *
     * @param options the options to start the pipeline
     * @return a single with the deployment description
     */
    private Single<PipelineDeployment> startLocalPipeline(PipelineOptions options) {
        String controlChannel = UUID.randomUUID().toString();
        return vertx.rxDeployVerticle(PIPELINE_VERTICLE, new DeploymentOptions().setConfig(options.toJson().put("controlChannel", controlChannel)))
                .doOnError(err -> {
                    log.error("An error occured while deploying pipeline {}", options);
                    log.error("Reason: ", err);
                })
                .map(deploymentId -> new PipelineDeployment()
                        .setId(deploymentId)
                        .setName(options.getName())
                        .setNode(deployChannel)
                        .setControlChannel(controlChannel))
                .doOnSuccess(deployment -> addDeployment(deployment, options))
                ;
    }

    /**
     * Stores the deployment in the shared data.
     *
     * @param deployment the deployment description
     * @param options    the pipeline options
     */
    private void addDeployment(PipelineDeployment deployment, PipelineOptions options) {
        pipelines.rxPut(deployment, options)
                .subscribe(() -> log.info("Pipeline {} started.", deployment));
    }

    private Completable validateName(String name) {
        return getDeployments()
                .flatMapCompletable(deployments -> {
                    if (deployments.stream().noneMatch(deployment -> deployment.getName().equals(name))) {
                        return Completable.complete();
                    } else {
                        return Completable.error(new ServiceException(DUPLICATE_NAME, "A pipeline with the name " + name + " is already deployed."));
                    }
                });
    }

    private Single<Set<PipelineDeployment>> getDeployments() {
        return Single.create(emitter -> pipelinesDelegate().keys(deploymentsAr -> {
            if (deploymentsAr.succeeded()) {
                emitter.onSuccess(deploymentsAr.result());
            } else {
                log.error("Unable to retrieve deployments...");
                emitter.onError(deploymentsAr.cause());
            }
        }));
    }

    private Single<PipelineDeployment> getDeployment(String pipeline) {
        return getDeployments().flatMap(deployments -> {
            Optional<PipelineDeployment> deploymentOptional = deployments.stream()
                    .filter(deployment -> deployment.getName().equals(pipeline))
                    .findFirst();

            return deploymentOptional.isPresent()
                    ? Single.just(deploymentOptional.get())
                    : Single.error(new ServiceException(PIPELINE_NOT_FOUND, "Unable to find pipeline " + pipeline));
        });

    }


    private Single<PipelineOptions> getOptions(PipelineDeployment deployment) {
        return pipelines.rxGet(deployment)
                .flatMap(options -> options != null ? Single.just(options) : Single.error(new ServiceException(PIPELINE_NOT_FOUND, "Unable to find pipeline " + deployment)));
    }

    @Override
    public void stopPipeline(PipelineDeployment deployment, Handler<AsyncResult<Void>> handler) {
        Single<PipelineDeployment> deploymentSingle = Single.just(deployment);
        if (!isValid(deployment)) {
            if (!hasName(deployment)) {
                handler.handle(ServiceException.fail(INVALID_DEPLOYMENT, "No name found in deployment.", deployment.toJson()));
                return;
            }
            deploymentSingle = getDeployment(deployment.getName());
        }

        deploymentSingle
                .flatMapCompletable(this::stopPipelineInternal)
                .subscribe(CompletableHelper.toObserver(handler));
    }

    /**
     * Indicates whether name is blank
     *
     * @param deployment the deployment description
     * @return <code>true</code> when name is not blank
     */
    private boolean hasName(PipelineDeployment deployment) {
        return StringUtils.isNotBlank(deployment.getName());
    }

    /**
     * Indicates whether deployment description is fully valid (fields are not blank)
     *
     * @param deployment the deployment description
     * @return <code>true</code> when description is valid
     */
    private boolean isValid(PipelineDeployment deployment) {
        return StringUtils.isNoneBlank(deployment.getControlChannel(), deployment.getId(), deployment.getName(), deployment.getNode());
    }

    /**
     * Check whether the deployment is running on local node or a remote,
     * then invokes the subsequent event.
     *
     * @param deployment the deployment description
     * @return a completable that finishes when the pipeline has been stopped
     */
    private Completable stopPipelineInternal(PipelineDeployment deployment) {
        if (deployChannel.equals(deployment.getNode())) {
            return undeployLocalPipeline(deployment);
        } else {
            return undeployRemotePipeline(deployment);
        }
    }

    private Completable undeployLocalPipeline(PipelineDeployment deployment) {
        return pipelines.rxRemove(deployment)
                .flatMapCompletable(options -> {
                    if (vertx.deploymentIDs().contains(deployment.getId())) {
                        return vertx.rxUndeploy(deployment.getId()).onErrorComplete();
                    }
                    return Completable.complete();
                })
                .doOnComplete(() -> log.info("undeployed pipeline [{}]", deployment));
    }

    private Completable undeployRemotePipeline(PipelineDeployment deployment) {
        if (!registrations.contains(deployment.getNode())) {
            return Completable.error(new ServiceException(NO_CLUSTER_MATCH, "Unable to find a matching node.", deployment.toJson()));
        }
        DeliveryOptions options = new DeliveryOptions().addHeader("action", DeployChannelActions.UNDEPLOY.name());
        return vertx.eventBus().rxSend(deployment.getNode(), deployment.getName(), options)
                .doOnSuccess(message -> log.info("undeployed pipeline [{}]", deployment))
                .toCompletable();
    }


    //endregion

    //region Service shutdown
    public Completable close() {
        return getDeployments()
                .flatMapObservable(Observable::fromIterable)
                .filter(deployment -> deployment.getNode().equals(deployChannel))
                .flatMapCompletable(this::undeployLocalPipeline, true)
                .onErrorComplete()
                .andThen(Completable.concatArray(unregisterServiceChannelConsumer(), unregisterDeployChannel()))
                .doOnComplete(() -> log.info("Pipeline service [{}] closed.", deployChannel));
    }

    private Completable unregisterServiceChannelConsumer() {
        return serviceChannelConsumer
                .rxUnregister()
                .andThen(Completable.fromAction(() -> {
                    JsonObject data = new JsonObject().put("service", deployChannel);
                    DeliveryOptions options = new DeliveryOptions().addHeader("action", ServiceChannelActions.UNREGISTER_SERVICE.name());
                    vertx.eventBus().publish(SERVICE_CHANNEL_ADDRESS, data, options);
                }));
    }

    private Completable unregisterDeployChannel() {
        return deployChannelConsumer.rxUnregister();
    }
    //endregion

    @SuppressWarnings("unchecked")
    private io.vertx.core.shareddata.AsyncMap<PipelineDeployment, PipelineOptions> pipelinesDelegate() {
        return (io.vertx.core.shareddata.AsyncMap<PipelineDeployment, PipelineOptions>) pipelines.getDelegate();
    }

}
