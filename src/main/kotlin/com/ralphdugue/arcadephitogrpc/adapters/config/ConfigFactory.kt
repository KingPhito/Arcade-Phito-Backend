package com.ralphdugue.arcadephitogrpc.adapters.config

import com.google.cloud.ServiceOptions
import com.google.cloud.resourcemanager.v3.ProjectsClient
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import com.ralphdugue.arcadephitogrpc.domain.config.entities.AdminConfig
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import com.ralphdugue.arcadephitogrpc.domain.config.entities.DatabaseConfig
import com.ralphdugue.arcadephitogrpc.domain.config.entities.JWTConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import java.io.File

object ConfigFactory {

    fun loadConfig(): ArcadePhitoConfig {
        val logger = KotlinLogging.logger {}
        val env = try {
            val secretClient = SecretManagerServiceClient.create()
            val projectId = ServiceOptions.getDefaultProjectId()
            val projectNumber = System.getenv("PROJECT_NUM") ?: projectId
            getSecret(secretClient, projectNumber, "env")
        } catch (e: Exception) {
            logger.info { "Error reading env secret from GCP. Using dev configuration." }
            "dev"
        }
        return try {
            when (env) {
                "prod" -> {
                    val secretClient = SecretManagerServiceClient.create()
                    val projectId = ServiceOptions.getDefaultProjectId()
                    val projectNumber = System.getenv("PROJECT_NUM") ?: projectId
                    logger.info { "Using production configuration" }
                    getProdConfig(secretClient, projectNumber)
                }
                else -> {
                    logger.info { "Using development configuration" }
                    Json.decodeFromString<ArcadePhitoConfig>(File("src/main/resources/config.dev.json").readText())
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error reading configuration file" }
            throw e
        }
    }

    private fun getProdConfig(secretClient: SecretManagerServiceClient, projectId: String): ArcadePhitoConfig {
        val logger = KotlinLogging.logger {}
        return try {
            ArcadePhitoConfig(
                port = getSecret(secretClient, projectId, "port").toInt(),
                db = DatabaseConfig(
                    host = getSecret(secretClient, projectId, "db-host"),
                    username = getSecret(secretClient, projectId, "db-user"),
                    password = getSecret(secretClient, projectId, "db-password"),
                    driver = getSecret(secretClient, projectId, "db-driver")
                ),
                jwt = JWTConfig(
                    secret = getSecret(secretClient, projectId, "jwt-secret"),
                    issuer = getSecret(secretClient, projectId, "jwt-issuer"),
                    audience = getSecret(secretClient, projectId, "jwt-audience"),
                    expiration = getSecret(secretClient, projectId, "jwt-expiration").toLong()
                ),
                admin = AdminConfig(
                    developerId = getSecret(secretClient, projectId, "admin-dev-id"),
                    email = getSecret(secretClient, projectId, "admin-email"),
                    apiKey = getSecret(secretClient, projectId, "admin-api-key"),
                    apiSecret = getSecret(secretClient, projectId, "admin-api-secret")
                )
            )
        } catch (e: Exception) {
            logger.error(e) { "Error retrieving configuration secrets from GCP" }
            throw e
        }
    }

    private fun getSecret(secretClient: SecretManagerServiceClient, projectId: String, secretId: String): String {
        val logger = KotlinLogging.logger {}
        return try {
            val secretVersionName = SecretVersionName.of(projectId, secretId, "latest")
            val secretPayload = secretClient.accessSecretVersion(secretVersionName).payload.data.toStringUtf8()
            secretPayload
        } catch (e: Exception) {
            logger.error(e) { "Error reading config secret $secretId from GCP" }
            throw e
        }
    }
}