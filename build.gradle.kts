plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.netflix.dgs.codegen") version "7.0.3"
}

group = "dev.natig"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

extra["springAiVersion"] = "1.0.0-M6"
extra["springCloudVersion"] = "2024.0.0"
extra["springdocOpenApiVersion"] = "2.8.0"
extra["kotlinxSerializationJsonVersion"] = "1.7.3"
extra["kotlinxCoroutinesReactorVersion"] = "1.10.1"
extra["kotlinxDateTimeVersion"] = "0.6.2"
extra["netflixDgsVersion"] = "10.0.1"
extra["nkonevMultipartSpringGraphqlVersion"] = "1.5.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springdocOpenApiVersion")}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${property("kotlinxSerializationJsonVersion")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${property("kotlinxCoroutinesReactorVersion")}")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:${property("kotlinxDateTimeVersion")}")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("name.nkonev.multipart-spring-graphql:multipart-spring-graphql:${property("nkonevMultipartSpringGraphqlVersion")}")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:${property("netflixDgsVersion")}")
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    jvmArgs = listOf("-XX:+UseZGC -XX:+ZGenerational")
}
