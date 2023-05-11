
# Java Logs Telemetry Example

This is a quick demo/example of how to use java agent auto-instrumentation
to ingest logs into the Splunk Observability Cloud.

## Logging

This repo shows how you can configure log4j2 with the 
[Splunk Distribution of OpenTelemetry Java](https://github.com/signalfx/splunk-otel-java) to 
send logs through an otel collector into the Splunk Observability Cloud. This example
is based heavily on existing documentation that lives in the community upstream 
repositories (see the [References](#references)) section below.

Although this example focuses primarily on log4j2, similar results can be had with
other popular Java logging frameworks, such as `slf4j`, `logback` or `java.util.logging` (`JUL`). 

We will use 3 terminal windows here:
* one to run the collector
* one to run the java application
* one to send traffic to the java application

## Collector config

By default, the agent will send telemetry to a collector running on localhost, so we'll need
to set that up first. Download and install the [`splunk-otel-collector`](https://github.com/signalfx/splunk-otel-collector)
and use the following pieces of configuration:

This is NOT a complete configuration, but you must make sure that your collector is configured
to receive OTLP via GRPC, and has configured a `splunk_hec` exporter.

Here is a relevant snippet for logs related collector config:
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
    endpoint: "https://ingest.us0.signalfx.com/v1/log"
service:
  pipelines:
    logs:
      receivers: [otlp]
      processors: [batch]
      exporters: [logging/debug, splunk_hec]
```

Run the collector in one terminal window and leave it open.

## Java app

The app can be run with the gradle wrapper provided:
```
./gradlew run
```

Run that in a separate terminal and leave it open.

The agent configuration is performed with java system properties passed on the 
commandline. See the [`build.gradle.kts`](build.gradle.kts) file for
the full details, but let's go over the important parts:

* `-javaagent:splunk-otel-javaagent-1.24.0.jar` - This wires up the java instrumentation agent to the application
* `-Dotel.logs.exporter=otlp` - This enables otel log exporting. 
* `-Dotel.resource.attributes=deployment.environment=logs-example` - This configures the environment name for our otel resource
* `-Dotel.service.name=LogsExample` - This configures the service name for our otel resource
* `-Dotel.javaagent.debug=true` - Optional. This only allows us to see more details in the java agent output. 

Another relevant piece of configuration is in the `LogsExampleMain.configureLogging()` method.
This method does several things:

1. Creates an instance of `OtlpGrpcLogRecordExporter` configured to send to localhost on port 4317 (OTLP default)
2. It creates a `BatchLogRecordProcessor` so that logs can be efficiently sent together in groups
3. It creates a resource based on the platform default and adds a custom `service.name`
4. It creates the instance of the `SdkLoggerProvider` implementation and registers it as the `GlobalLoggerProvider`.

### log4j2.xml

The `log4j2.xml` configuration contains 3 primary bits that help to make this work:

* `<Configuration status="DEBUG" packages="io.opentelemetry.instrumentation.log4j.appender.v2_17">` - This includes the log4j2 package for the otel appender
* `<OpenTelemetry name="OpenTelemetryAppender"/>` - This defines the `OpenTelemetryAppender` as an available appender to use.
* `<AppenderRef ref="OpenTelemetryAppender" />` - This includes the aforementioned appender in the root appender.

## Web API

This app has a very simple web api (port 8182) that is used to generate spans and logs:

```
curl 'http://localhost:8182/greeting?name=meep&num=5'
```

Where `name` is the name to echo and `num` is the number of emoji to return.
The `num` also implies a delay, so larger num takes longer to return (longer spans).

For every request, a single span will be created and 3 messages will be logged.

## Output

So what does it look like?

Firstly, the console appender will show some lines that look like this:
```
14:20:00.000 [qtp1792008325-56] ea45af44a2aafa0931975abfb701d2e0:2943cf2d70e166f9:01 INFO  com.splunk.example.Server - Got request from meep
14:20:00.000 [qtp1792008325-56] ea45af44a2aafa0931975abfb701d2e0:2943cf2d70e166f9:01 INFO  com.splunk.example.Server - meep has requested 5 random emoji
14:20:01.011 [qtp1792008325-56] ea45af44a2aafa0931975abfb701d2e0:2943cf2d70e166f9:01 INFO  com.splunk.example.Server - Sending response to meep: Hello meep
🍻 🚹 😶 ❔ ↩
```

The 3rd field is the colon `:` delimited MDC (mapped diagnostic context), which 
contains the trace id, span id, and flags as configured in the `PatternLayout` in 
log4j2.xml The presence of this data confirms that MDC is wired up and working.

If we open Log Observer and filter on `service.name = LogsExample` we see that logs have indeed come into Splunk o11y cloud:

<img width="1005" alt="image" src="https://github.com/breedx-splk/java-logs-example/assets/75337021/13ad990f-74e2-4a04-9988-8f731755d7fb">

And if we click on one of the log lines we can see our log message along with the 
individual fields.
<img width="540" alt="image" src="https://github.com/breedx-splk/java-logs-example/assets/75337021/78065234-947a-4112-8eea-23f3d2f0bea2">

At the top of the detail list is a clickable trace id:

<img width="444" alt="image" src="https://github.com/breedx-splk/java-logs-example/assets/75337021/b343cc43-70de-4fbc-8d5c-8065d36c8dea">

and if you choose "View trace_id" you will navigate to the detailed trace view, showing our 
simple single-span trace results:

<img width="638" alt="image" src="https://github.com/breedx-splk/java-logs-example/assets/75337021/323da693-91f5-4c33-a8fa-c7f0e48f7595">

# Summary

As of this writing, logs are still somewhat new in OpenTelemetry and have not reached maturity. 
However, we have demonstrated that there is already useful support for the logs signal.
You can leverage the instrumentation agent and otel SDK to both generate MDC for local logs, AND
you can send logs with trace context into an otel collector and forwarded to Splunk o11y.

# References:

* [log4j-appender-2.17 docs](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/main/instrumentation/log4j/log4j-appender-2.17/library/README.md)
* [log appender info in docs repo](https://github.com/open-telemetry/opentelemetry-java-docs/tree/main/log-appender)
