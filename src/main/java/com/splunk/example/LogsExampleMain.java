package com.splunk.example;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.logs.GlobalLoggerProvider;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.logs.export.SimpleLogRecordProcessor;
import io.opentelemetry.sdk.resources.Resource;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

public class LogsExampleMain {

    public static void main(String[] args) {

        LogRecordExporter exporter = OtlpGrpcLogRecordExporter.builder()
                .setEndpoint("http://localhost:4317")
                .build();
        LogRecordProcessor processor = SimpleLogRecordProcessor.create(exporter);
        Resource resource = Resource.create(Attributes.of(stringKey("test"), "test-val"));
        SdkLoggerProvider sdkLoggerProvider = SdkLoggerProvider.builder()
                .setResource(resource)
                .addLogRecordProcessor(processor)
                .build();
        GlobalLoggerProvider.set(sdkLoggerProvider);

        new Server().runForever();
    }
}
