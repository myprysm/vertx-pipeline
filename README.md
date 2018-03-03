# Vert.x Pipeline

An Event Stream Processing platform, highly scalable, resilient, and asynchronous by design.

## What's Vert.x Pipeline

Vert.x Pipeline looks much like Vert.x, acting as a toolbox, toolkit, low-resource consuming,
fast and non blocking flow processor. It consists in building a set of scalable `pipelines` 
through a configuration descriptor, running the system as a standalone application 
as well as embedding it into your own stuff. Each element of the pipeline is a single `verticle` 
and relies on Vert.x backbone.

A `pipeline` is a set of one `pump`, zero to n `processors`, and one `sink`. 
It is meant to emit events on Vert.x `event bus` from a `pump` to a `sink`. 
Those events can be intercepted by any kind of `processor` configured in the chain 
to perform any action as described below.

![Vert.x Pipeline](./vertx-pipeline.png)


For easier data manipulation, this first version (more of a POC at the moment) communicates its events
only through `JsonObject` events. It is planned to support other formats like `Avro`, `Protobuf` or `Thrift`.

Those `verticles` can be configured quickly and easily from a simple `json` or `yaml` file describing
the event source (`pump`), the operations to execute on it (`processor`), and finally the output (`sink`).

It tries to leverage Vert.x with reactive streams (RxJava2) for a better flow control and backpressure capabilities.
Each `verticle` of the `pipeline` is addressed in the chain automatically during deployment 
on the `event bus` and is meant to perform either long/blocking computations or small things 
but also and mainly a **single operation** on an event.
This can come from extracting data to accumulate events before aggregating the results when a termination signal is sent.

Vert.x Pipeline embeds loads of `pump`, `processor`, and `sink` in its core, 
but if those `verticles` are not enough, you can also re-use and augment this toolkit with your own.
This is easy as a pie.

Please check the documentation generated aside to sources to discover all the `verticles` you can put
into your pipelines and their options.

## Configuring pipeline

As explained above even though a `pipeline` has no limit in its internal chain, 
it requires only one input channel (`pump`) and one output channel (`sink`).

Here is an example of a simple pipeline that will pump a timer every 100ms and this will output its data
into a json file:

```
simple-timer-processor:
  pump:
    type: fr.myprysm.pipeline.pump.TimerPump
    interval: 100
    unit: MILLISECONDS
  processors:
    - type: fr.myprysm.pipeline.processor.DataExtractorProcessor
      extract:
        counter: another.field.counter
        timestamp: that.damn.works
    - type: fr.myprysm.pipeline.processor.LogProcessor
      level: INFO
  sink:
    type: fr.myprysm.pipeline.sink.FileSink
    path: /some/path/to/store
    # Extension will be guessed from output type
    file: timer-output
    # JSON is the default output format of this sink
    type: json
```


## Developing new veticles

Vert.x Pipeline provides an enhanced `ConfigurableVerticle<T extends Options>` as well as 
`BaseJsonPump<T extends ProcessorOptions>`, `BaseJsonProcessor<T extends ProcessorOptions>` 
and `BaseJsonSink<T extends ProcessorOptions>`. They provide a simple lifecycle that is invoked
when a `PipelineVerticle` is (un)deployed across the instance(s). 
It is meant to avoid some boilerplate code while keeping some control and awareness
of what should do the `verticle` to execute fully and be operable inside of a running `pipeline`.

Startup of a `ConfigurableVerticle` occurs as follows:

![ConfigurableVerticle<T extends Options> startup](./configurable-verticle-startup.png)

Shutdown of a `ConfigurableVerticle` occurs as follows:

![ConfigurableVerticle<T extends Options> shutdown](./configurable-verticle-shutdown.png)

Each base implementation of a `pump`, `processor`, `sink` comes with some defaults for thoses steps
of the lifecycle but you can obviously override almost everything. It provides a good and quick start
to develop new components/nodes to enhance your network of pipelines. As you can notice this lifecycle
is also quite simple and tries to stick to Vert.x `Verticle`.

More deeper a processor that does nothing else but forwarding the messages as they come looks like:
```
package fr.myprysm.pipeline.processor;

import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

/**
 * A processor that does nothing.
 * It emits the items as they come.
 */
public final class NoOpProcessor extends BaseJsonProcessor<ProcessorOptions> {

    @Override
    public Single<JsonObject> transform(JsonObject input) {
        return Single.just(input);
    }

    @Override
    protected Completable startVerticle() {
        return Completable.complete();
    }

    @Override
    public ProcessorOptions readConfiguration(JsonObject config) {
        return new ProcessorOptions(config);
    }

    @Override
    public Completable configure(ProcessorOptions config) {
        return Completable.complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return ValidationResult.valid();
    }
}
```
You can look into sources to see examples of `sink` and `pump`.

While providing default options for each kind of components each `verticle` should describe
its specific options with validators (using `JsonValidation` and `JsonHelper`)
to ensure a proper run and the pipeline stability.

## In the pipe

- [ ] `ForkProcessor`                   - duplicates the signal to another `pipeline`
- [x] `DataExtractorProcessor`          - extracts and transform input event to a new brand output event
- [ ] `KeyToValueListProcessor`         - maps some fields of the input as key and some other field (may be the input) as value in a list.
                                        this processor is an accumulator, thus it needs to receive a signal to flush its data.
                                        key as well as value can be any kind of object but a `null` value.
- [ ] `ObjectToArrayProcessor`          - transforms a field into an array. When field is null, array is empty, 
                                        otherwise array contains the previous field value
- [ ] `CounterSignalEmitterProcessor`   - Emits a signal to the `PipelineVerticle` controller every n received events 
                                        to either broadcast a flush, or terminate the pipeline
- [ ] `TimerSignalEmitterProcessor`     - Emits a signal to the `PipelineVerticle` controller after configured duration
                                        to either broadcast a flush, or terminate the pipeline

## Next to come

- [ ] Pipeline pause/resume
- [ ] Pipeline hot redeployment
- [ ] `PumpAndSink` - output to your input!! (thus web handlers can be quickly configured and their behaviour described)
- [ ] Metrics pipeline - know you throughput and your success rate
- [ ] Web interface - check the status, reconfigure
- [ ] More processors...
- [ ] More sinks...
- [ ] More pumps...
