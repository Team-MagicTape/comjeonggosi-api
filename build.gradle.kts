import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.4"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "dev.comgo"
version = "1.0.0"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}


extra["snippetsDir"] = file("build/generated-snippets")
extra["springGrpcVersion"] = "0.9.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
//    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("software.amazon.awssdk:ses:2.32.19")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.9")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.grpc:grpc-services")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework:spring-jdbc")
    implementation("org.springframework.grpc:spring-grpc-spring-boot-starter")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
//    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.springframework.graphql:spring-graphql-test")
    testImplementation("org.springframework.grpc:spring-grpc-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.grpc:spring-grpc-dependencies:${property("springGrpcVersion")}")
    }
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc") {
                    option("@generated=omit")
                }
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    dependsOn(tasks.test)
    sourceDir("src/docs/asciidoc")
    attributes(
        mapOf(
            "snippets" to project.extra["snippetsDir"]
        )
    )
}

tasks.bootJar {
    archiveBaseName.set("comjeonggosi-api")
    archiveVersion.set("")
    archiveClassifier.set("")
}