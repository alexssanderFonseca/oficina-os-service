plugins {
	java
	id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "6.2.0.5505"
    id("jacoco")
    id("io.freefair.lombok") version "8.4"
}

group = "br.com.oficina"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

dependencyManagement {
    imports {
        mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.23.0")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2025.1.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation ("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("com.h2database:h2")
    implementation("org.mapstruct:mapstruct:1.6.3")
    implementation("org.postgresql:postgresql:42.7.3")
	testImplementation("org.springframework.boot:spring-boot-starter-opentelemetry-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.cucumber:cucumber-java:7.18.0")
    testImplementation("io.cucumber:cucumber-spring:7.18.0")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.18.0")
    testImplementation("io.rest-assured:rest-assured:5.4.0")

    testImplementation("org.junit.platform:junit-platform-suite:1.11.0-M1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:2.23.0-alpha")
    runtimeOnly("io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:2.23.0-alpha")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
tasks.withType<Test> {
    maxParallelForks = 1
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
    jvmArgs("-Dotel.traces.exporter=none", "-Dotel.metrics.exporter=none") // Disable OTLP exporters for tests
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}


tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(false)
        csv.required.set(false)
    }
}

sonar {
    properties {
        property("sonar.tests", "src/test/java")
        property("sonar.junit.reportPaths", "build/test-results/test")
        property("sonar.test.exclusions", "src/test/java/**/*.java")
        property(
            "sonar.exclusions",
            "src/main/java/**/security/**,src/main/java/**/commons/**,src/main/java/**/request/**,src/main/java/**/response/**,src/main/java/**/input/**,src/main/java/**/output/**," +
                    "src/main/java/**/exceptions/**,src/main/java/**/handler/**"
        )
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

jacoco {
    toolVersion = "0.8.12"
}