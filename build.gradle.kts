import com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlExtension

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version : String by project
val h2_version : String by project
val koin_version : String by project
val mockk_version : String by project
val kgraphql_version : String by project
val graphql_kotlin_version : String by project
val pgjdbc_version : String by project

plugins {
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version "2.3.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.google.cloud.tools.appengine") version "2.4.2"
    id("com.expediagroup.graphql") version "6.5.3"
}

group = "com.ralphdugue.arcadephito"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17
application {
    mainClass.set("com.ralphdugue.arcadephito.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("com.h2database:h2:$h2_version")
    implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:$pgjdbc_version")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("io.ktor:ktor-server-swagger-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-openapi:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-apache-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
//    implementation("com.apurebase:kgraphql:$kgraphql_version")
//    implementation("com.apurebase:kgraphql-ktor:$kgraphql_version")
    implementation("com.expediagroup", "graphql-kotlin-ktor-server", graphql_kotlin_version)
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.2")
    testImplementation("io.mockk:mockk:$mockk_version")
}

graphql {
    schema {
        packages = listOf("om.ralphdugue.arcadephito.backend")
    }
}