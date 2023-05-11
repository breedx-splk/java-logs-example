package com.splunk.example;

import com.google.auto.service.AutoService;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.logs.SdkLoggerProviderBuilder;

import java.util.function.BiFunction;

@AutoService(AutoConfigurationCustomizerProvider.class)
public class LogsSdkProviderCustomizer implements AutoConfigurationCustomizerProvider {
    @Override
    public void customize(AutoConfigurationCustomizer autoConfiguration) {
        System.out.println("MF MF MF");

        autoConfiguration.addLoggerProviderCustomizer(new BiFunction<SdkLoggerProviderBuilder, ConfigProperties, SdkLoggerProviderBuilder>() {
            @Override
            public SdkLoggerProviderBuilder apply(SdkLoggerProviderBuilder sdkLoggerProviderBuilder, ConfigProperties configProperties) {
                return sdkLoggerProviderBuilder;
            }
        });
    }
}
