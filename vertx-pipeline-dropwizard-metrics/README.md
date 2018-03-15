# Dropwizard Metrics for Vert.x Pipeline

Metrics Service Provider implementations for Vert.x Pipeline using [Dropwizard metrics](https://github.com/dropwizard/metrics) library.

This implementation is fully inspired by the original [Dropwizard Metrics for Vert.x](https://github.com/vert-x3/vertx-dropwizard-metrics)

Metrics can be exposed through JMX under `vertx-pipeline`. 
Counts as `ThroughputMeter` the events received, sent and processed with error.



