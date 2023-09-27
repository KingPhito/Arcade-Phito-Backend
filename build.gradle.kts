import com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlExtension
import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

val kotlin_version: String by project
val koin_version : String by project
val mockk_version : String by project
val sqldelight_version : String by project
val grpc_version : String by project
val grpc_kotlin_version : String by project
val protobuf_version : String by project
val h2_version : String by project

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("com.google.protobuf") version "0.9.4"
    id("app.cash.sqldelight") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.google.cloud.tools.appengine") version "2.4.2"
}

group = "com.ralphdugue.arcadephito-grpc"
version = "1.0-SNAPSHOT"

appengine {
    configure<AppEngineAppYamlExtension> {
        stage {
            setArtifact("build/libs/${project.name}-all.jar")
        }
        deploy {
            version = "GCLOUD_CONFIG"
            projectId = "GCLOUD_CONFIG"
        }
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    implementation("io.grpc:grpc-kotlin-stub:$grpc_kotlin_version")
    implementation("io.grpc:grpc-protobuf:$grpc_version")
    implementation("io.grpc:grpc-netty:$grpc_version")
    implementation("com.google.protobuf:protobuf-java:$protobuf_version")
    implementation("com.google.protobuf:protobuf-kotlin:$protobuf_version")
    implementation("com.h2database:h2:$h2_version")
    implementation("org.postgresql:postgresql:42.5.4")
    implementation("app.cash.sqldelight:jdbc-driver:2.0.0")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("com.auth0:java-jwt:4.4.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.2")
    testImplementation("io.mockk:mockk:$mockk_version")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobuf_version"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpc_version"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${grpc_kotlin_version}:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

sqldelight {
    databases {
        create("ArcadePhito") {
            packageName = "com.ralphdugue.arcadephitogrpc"
            dialect("app.cash.sqldelight:postgresql-dialect:2.0.0")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}