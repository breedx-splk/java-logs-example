plugins {
    application
}

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile> {
    val compilerArgs = options.compilerArgs
    //NOTE: This is required for @P
    compilerArgs.addAll(listOf("-parameters"))
}
application {
    mainClass.set("com.splunk.example.LogsExampleMain")
    applicationDefaultJvmArgs = listOf(
        "-javaagent:splunk-otel-javaagent-1.24.0.jar",
        "-Dotel.javaagent.debug=true",
        "-Dotel.resource.attributes=deployment.environment=logs-example",
        "-Dotel.service.name=LogsExample",
        "-Dotel.logs.exporter=otlp",
        "-Dotel.instrumentation.log4j-appender.enabled=true",
        "-Dotel.instrumentation.log4j-mdc.enabled=true"
    )
}

dependencies {
    implementation("com.sparkjava:spark-core:2.9.4")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")

    annotationProcessor("com.google.auto.service:auto-service:1.0.1")
    compileOnly("com.google.auto.service:auto-service:1.0.1")

    compileOnly("io.opentelemetry:opentelemetry-api-logs:1.25.0-alpha")
    implementation("io.opentelemetry:opentelemetry-sdk-logs:1.25.0-alpha")
    implementation("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure:1.25.0-alpha")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp-logs:1.25.0-alpha")
    runtimeOnly("io.opentelemetry.instrumentation:opentelemetry-log4j-appender-2.17:1.25.1-alpha")
}
