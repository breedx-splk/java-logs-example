package com.splunk.example;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.logs.GlobalLoggerProvider;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.resources.Resource;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

public class LogsExampleMain {

    public static void main(String[] args) {
        configureLogging();
        new Server().runForever();
    }

    private static void configureLogging() {
        LogRecordExporter exporter = OtlpGrpcLogRecordExporter.builder()
                .setEndpoint("http://localhost:4317")
                .build();
        LogRecordProcessor processor = BatchLogRecordProcessor.builder(exporter)
                .build();
        Attributes resourceAttributes = Attributes.of(stringKey("service.name"), "LogsExample");
        Resource resource = Resource.getDefault().merge(Resource.create(resourceAttributes));

        SdkLoggerProvider sdkLoggerProvider = SdkLoggerProvider.builder()
                .setResource(resource)
                .addLogRecordProcessor(processor)
                .build();
        GlobalLoggerProvider.set(sdkLoggerProvider);
    }
}
