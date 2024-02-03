import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.shadow)
    alias(libs.plugins.ksp)
    alias(libs.plugins.version.catalog.update)
}

group = "com.ralphdugue.arcadephito-grpc"
version = "1.0-SNAPSHOT"

shadow {
    archivesName.set("${project.name}-fat")
    version = ""
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    val koinBom = platform(libs.koin.bom)
    implementation(koinBom)
    val gcloudBom = platform(libs.gcloud.bom)
    implementation(gcloudBom)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization)
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-logger-slf4j")
    implementation(libs.gcloud.logback)
    implementation(libs.kotlin.logging)
    implementation(libs.bundles.grpc)
    implementation(libs.postgres)
    implementation(libs.bundles.sqldelight)
    implementation(libs.hikari)
    implementation(libs.auth0.jwt)
    implementation("com.google.cloud:google-cloud-secretmanager")
    implementation("com.google.cloud:google-cloud-core")
    implementation("com.google.cloud:google-cloud-resourcemanager")
    implementation("org.springframework.security:spring-security-core:6.0.8")
    testImplementation(libs.bundles.test)
}

sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.60.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

sqldelight {
    databases {
        create("ArcadePhitoDB") {
            packageName.set("com.ralphdugue.arcadephitogrpc")
            dialect("app.cash.sqldelight:postgresql-dialect:2.0.1")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "com.ralphdugue.arcadephitogrpc.AppKt"
    }
    mergeServiceFiles()
}

kotlin {
    jvmToolchain(17)
}
