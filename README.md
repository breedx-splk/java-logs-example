
# Java Logs Telemetry Example

This is a quick demo/example of how to use java agent auto-instrumentation
to ingest logs into the Splunk Observability Cloud.

# Logging

tbd - work in progress

## Collector config

This is a snippet:
```
receivers:
  otlp:
    protocols:
      grpc:
exporters:
  logging/debug:
    loglevel: debug
  splunk_hec:
    token: "${SFX_TOKEN}"
    endpoint: "https://ingest.lab0.signalfx.com/v1/log"
service:
  pipelines:
    logs:
      receivers: [otlp]
      processors: [batch]
      exporters: [logging/debug, splunk_hec]
```

## Web API

```
curl 'http://localhost:8182/greeting?name=meep&num=5'
```

Where `name` is the name to echo and `num` is the number of emoji to return.
The `num` also implies a delay, so larger num takes longer to return (longer spans).

# References:

* [log4j-appender-2.17 docs](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/main/instrumentation/log4j/log4j-appender-2.17/library/README.md)
* [log appender info in docs repo](https://github.com/open-telemetry/opentelemetry-java-docs/tree/main/log-appender)