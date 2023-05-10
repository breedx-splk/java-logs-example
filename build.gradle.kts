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
        "-javaagent:splunk-otel-javaagent-1.23.1.jar",
        "-Dotel.javaagent.debug=true",
        "-Dotel.resource.attributes=deployment.environment=logs-example",
        "-Dotel.service.name=LogsExample",
        "-Dotel.logs.exporter="
    )
}

dependencies {
    implementation("com.sparkjava:spark-core:2.9.4")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
}
