package fr.myprysm.pipeline.pipeline.impl;

import com.google.common.collect.ImmutableSet;
import fr.myprysm.pipeline.pipeline.*;
import fr.myprysm.pipeline.util.EnumUtils;
import fr.myprysm.pipeline.util.Signal;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.CompletableHelper;
import io.vertx.reactivex.SingleHelper;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.core.impl.AsyncResultSingle;
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
@SuppressWarnings({"ResultOfMethodCallIgnored", "FieldCanBeLocal"})
@Slf4j
public class PipelineServiceImpl implements PipelineService {


    public static int PIPELINE_NOT_FOUND = 404;
    public static int DUPLICATE_NAME = 405;
    public static int NO_CLUSTER_MATCH = 409;
    public static int INVALID_OPTIONS = 416;
    public static int INVALID_DEPLOYMENT = 417;
    public static int DEPLOYMENT_ERROR = 418;

    private static final String PIPELINES_MAP = "fr.myprysm.pipeline:pipelines";
    private static final String REGISTRATIONS_MAP = "fr.myprysm.pipeline:service-registrations";

    public static final String PIPELINE_VERTICLE = "fr.myprysm.pipeline.pipeline.PipelineVerticle";


    private final Vertx vertx;
    private final String deployChannel;
    private final boolean clustered;
    private MessageConsumer<String> deployChannelConsumer;
    /**
     * The deployments across the nodes.
     */
    private AsyncMap<PipelineDeployment, PipelineOptions> pipelines;
    private AsyncMap<String, String> registrations;

    public PipelineServiceImpl(Vertx vertx, String deployChannel) {
        this.vertx = vertx;
        this.deployChannel = deployChannel;
        clustered = vertx.isClustered();
    }

    //region Configuration
    public Completable configure() {
        return Completable.concatArray(
                preparePipelinesMap(),
                prepareRegistrationsMap(),
                prepareDeployChannelConsumer()
        )
                .andThen(Completable.defer(this::registerService));
    }

    private Completable registerService() {
        return registrations.rxPut(deployChannel, deployChannel);
    }

    private Completable prepareRegistrationsMap() {
        return vertx.sharedData()
                .<String, String>rxGetAsyncMap(REGISTRATIONS_MAP)
                .doOnSuccess(this::setRegistrations)
                .toCompletable();
    }

    private void setRegistrations(AsyncMap<String, String> registrations) {
        this.registrations = registrations;
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

    private Completable prepareDeployChannelConsumer() {
        deployChannelConsumer = vertx.eventBus().consumer(deployChannel, this::deployChannelActionDispatcher);
        return Completable.complete();
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
            }
        }

    }

    private void handleDeploy(Message<String> message) {
        if (message.headers().get("from") != null) {
            log.info("[{}] Service [{}] requests to start a pipeline", deployChannel, message.headers().get("from"));
        }
        PipelineOptions options = new PipelineOptions(new JsonObject(message.body()));
        startPipelineInternal(options, false)
                .map(PipelineDeployment::toJson)
                .subscribe(
                        deployment -> {
                            if (message.replyAddress() != null) {
                                message.reply(deployment.toString());
                            }
                        },
                        error -> {
                            if (message.replyAddress() != null) {
                                message.reply(error);
                            }
                        }
                );
    }

    private void handleUndeploy(Message<String> pipeline) {
        getDeployments()
                .flatMapObservable(Observable::fromIterable)
                .filter(deployment -> deployment.getNode().equals(deployChannel) && deployment.getName().equals(pipeline.body()))
                .firstOrError()
                .flatMapCompletable(deployment -> undeployLocalPipeline(deployment, true))
                .subscribe(() -> {
                    log.info("Undeployed [{}].", pipeline.body());
                    if (pipeline.replyAddress() != null) {
                        pipeline.reply("acknowledged");
                    }
                });
    }
    //endregion


    //region Service implementation


    @Override
    public void getNodes(Handler<AsyncResult<Set<String>>> handler) {
        registrationsDelegate().keys(handler);
    }

    @Override
    public void getRunningPipelines(Handler<AsyncResult<Set<PipelineDeployment>>> handler) {
        pipelinesDelegate().keys(handler);
    }

    @Override
    public void getPipelineDescription(PipelineDeployment deployment, Handler<AsyncResult<PipelineOptions>> handler) {
        Single<PipelineOptions> options;
        if (isValid(deployment)) {
            options = getOptions(deployment);
        } else if (hasName(deployment)) {
            options = getDeployment(deployment.getName()).flatMap(this::getOptions);
        } else {
            options = Single.error(new ServiceException(INVALID_DEPLOYMENT, "No name found in deployment.", deployment.toJson()));
        }
        options.subscribe(SingleHelper.toObserver(handler));
    }

    @Override
    public void startPipeline(PipelineOptions options, String node, Handler<AsyncResult<PipelineDeployment>> handler) {
        Single<PipelineDeployment> deployment;
        if (StringUtils.isBlank(node)) {
            deployment = startPipelineInternal(options, true);
        } else if (deployChannel.equals(node)) {
            deployment = startPipelineInternal(options, false);

        } else {
            deployment = getRegistration(node)
                    .map(ImmutableSet::of)
                    .flatMap(set -> tryStartOnCluster(options, set.iterator()));
        }

        deployment.subscribe(SingleHelper.toObserver(handler));
    }

    /**
     * Starts the pipeline with the provided configuration.
     * <p>
     * In cluster mode, the flag <code>tryCluster</code> indicates whether the service should try to request other
     * registered services to start the pipeline.
     *
     * @param options    the pipeline options
     * @param tryCluster indicates whether the deployment should be tried on the cluster
     */
    private Single<PipelineDeployment> startPipelineInternal(PipelineOptions options, boolean tryCluster) {
        ValidationResult validation = PipelineOptionsValidation.validate(options.toJson());
        Single<PipelineDeployment> deployment;
        if (validation.isValid()) {
            deployment = validateName(options.getName())
                    .andThen(startLocalPipeline(options));
        } else if (clustered && tryCluster) {
            deployment = getRegistrations()
                    .map(registrations -> {
                        registrations.remove(deployChannel);
                        return registrations;
                    })
                    .flatMap(registrations -> tryStartOnCluster(options, registrations.iterator()));
        } else {
            deployment = Single.error(new ServiceException(INVALID_OPTIONS, "Options for pipeline " + options.getName() + " are invalid: " + validation.getReason().get(), options.toJson()));
        }

        return deployment;
    }

    private Single<PipelineDeployment> tryStartOnCluster(PipelineOptions options, Iterator<String> registrations) {
        if (!registrations.hasNext()) {
            return Single.error(new ServiceException(NO_CLUSTER_MATCH, "Unable to find a matching node to deploy the pipeline...", options.toJson()));
        }
        String next = registrations.next();
        return actionDeploy(options, next).onErrorResumeNext(tryStartOnCluster(options, registrations));
    }

    private Single<PipelineDeployment> actionDeploy(PipelineOptions options, String remote) {
        DeliveryOptions deliveryOptions = new DeliveryOptions()
                .addHeader("action", DeployChannelActions.DEPLOY.name())
                .addHeader("from", deployChannel);

        return vertx.eventBus()
                .<String>rxSend(remote, options.toJson().toString(), deliveryOptions)
                .map(message -> new PipelineDeployment(new JsonObject(message.body())));
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
        DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(options.setDeployChannel(deployChannel).toJson().put("controlChannel", controlChannel));

        return vertx.rxDeployVerticle(PIPELINE_VERTICLE, deploymentOptions)
                .onErrorResumeNext(err -> {
                    log.error("An error occured while deploying pipeline {}", options);
                    log.error("Reason: ", err);
                    if (!(err instanceof ServiceException)) {
                        err = new ServiceException(DEPLOYMENT_ERROR, "An error occured while deploying pipeline '" + options.getName() + "': " + err.getMessage(), options.toJson());
                    }

                    return Single.error(err);
                })
                .map(deploymentId -> new PipelineDeployment()
                        .setId(deploymentId)
                        .setName(options.getName())
                        .setNode(deployChannel)
                        .setControlChannel(controlChannel))
                .flatMap(deployment -> addDeployment(deployment, options));
    }

    /**
     * Stores the deployment in the shared data.
     *
     * @param deployment the deployment description
     * @param options    the pipeline options
     */
    private Single<PipelineDeployment> addDeployment(PipelineDeployment deployment, PipelineOptions options) {
        return pipelines.rxPut(deployment, options)
                .doOnComplete(() -> log.debug("Successfully added pipeline {} with config {}", deployment, options))
                .andThen(Single.just(deployment));
    }

    private Completable validateName(String name) {
        return getDeployments()
                .flatMapCompletable(deployments -> {
                    if (deployments.stream().noneMatch(deployment -> deployment.getName().equals(name))) {
                        return Completable.complete();
                    } else {
                        return Completable.error(new ServiceException(DUPLICATE_NAME, "A pipeline with the name '" + name + "' is already deployed."));
                    }
                });
    }

    @Override
    public void stopPipeline(PipelineDeployment deployment, Handler<AsyncResult<Void>> handler) {
        if (deployment == null || !hasName(deployment)) {
            handler.handle(ServiceException.fail(INVALID_DEPLOYMENT, "No name found in deployment."));
            return;
        }

        Single<PipelineDeployment> deploymentSingle = isValid(deployment) ? Single.just(deployment) : getDeployment(deployment.getName());

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
     * Requests the pipeline to stop.
     *
     * @param deployment the deployment description
     * @return a completable that finishes when the pipeline has been stopped
     */
    private Completable stopPipelineInternal(PipelineDeployment deployment) {
        DeliveryOptions options = new DeliveryOptions().setSendTimeout(1000L);
        return vertx.eventBus()
                .rxSend(deployment.getControlChannel(), Signal.TERMINATE.name(), options)
                .toCompletable();
    }

    private Completable undeployLocalPipeline(PipelineDeployment deployment, boolean undeploy) {
        return pipelines.rxRemove(deployment)
                .flatMapCompletable(options -> {
                    if (undeploy && vertx.deploymentIDs().contains(deployment.getId())) {
                        return vertx.rxUndeploy(deployment.getId()).onErrorComplete();
                    }
                    return Completable.complete();
                })
                .doOnComplete(() -> log.info("undeployed pipeline [{}]", deployment));
    }


    //endregion

    //region Service shutdown
    public Completable close() {
        return close(true);
    }

    public Completable close(boolean undeploy) {
        return getDeployments()
                .flatMapObservable(Observable::fromIterable)
                .filter(deployment -> deployment.getNode().equals(deployChannel))
                .flatMapCompletable(deployment -> undeployLocalPipeline(deployment, undeploy), true)
                .onErrorComplete()
                .andThen(Completable.concatArray(unregisterService(), unregisterDeployChannel()))
                .doOnComplete(() -> log.info("Pipeline service [{}] closed.", deployChannel));
    }

    private Completable unregisterService() {
        return registrations.rxRemove(deployChannel).toCompletable();
    }

    private Completable unregisterDeployChannel() {
        return deployChannelConsumer.rxUnregister();
    }
    //endregion

    //region utils
    private Single<Set<String>> getRegistrations() {
        return new AsyncResultSingle<>(handler -> registrationsDelegate().keys(handler));
    }

    private Single<String> getRegistration(String registration) {
        return getRegistrations().flatMap(registrations -> {
            if (registrations.contains(registration)) {
                return Single.just(registration);
            }
            return Single.error(new ServiceException(NO_CLUSTER_MATCH, "Registration '" + registration + "' not found."));
        });
    }

    private Single<Set<PipelineDeployment>> getDeployments() {
        return new AsyncResultSingle<>(handler -> pipelinesDelegate().keys(handler));
    }

    /**
     * Get the deployment associated with the pipeline name when it can be found.
     *
     * @param pipeline the pipeline name
     * @return a single that completes with the deployment description
     */
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

    /**
     * Get the options associated with the deployment when it can be found
     *
     * @param deployment the deployment
     * @return a single that completes with the options
     */
    private Single<PipelineOptions> getOptions(PipelineDeployment deployment) {
        return pipelines.rxGet(deployment)
                .flatMap(options -> options != null ? Single.just(options) : Single.error(new ServiceException(PIPELINE_NOT_FOUND, "Unable to find pipeline " + deployment)));
    }

    @SuppressWarnings("unchecked")
    private io.vertx.core.shareddata.AsyncMap<PipelineDeployment, PipelineOptions> pipelinesDelegate() {
        return (io.vertx.core.shareddata.AsyncMap<PipelineDeployment, PipelineOptions>) pipelines.getDelegate();
    }

    @SuppressWarnings("unchecked")
    private io.vertx.core.shareddata.AsyncMap<String, String> registrationsDelegate() {
        return (io.vertx.core.shareddata.AsyncMap<String, String>) registrations.getDelegate();
    }
    //endregion
}
