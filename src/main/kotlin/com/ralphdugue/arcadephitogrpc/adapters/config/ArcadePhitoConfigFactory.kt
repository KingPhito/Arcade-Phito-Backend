package com.ralphdugue.arcadephitogrpc.adapters.config

import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import java.io.File

object ArcadePhitoConfigFactory {

    fun create(): ArcadePhitoConfig {
        val logger = KotlinLogging.logger {}
        val env = System.getenv("ENVIRONMENT") ?: "dev"
        val file = when (env) {
                "prod" -> {
                    logger.info { "Using production configuration" }
                    "config.prod.json"
                }
                else -> {
                    logger.info { "Using development configuration" }
                    "config.dev.json"
                }
        }
        return try {
            Json.decodeFromString<ArcadePhitoConfig>(File("src/main/resources/$file").readText())
        } catch (e: Exception) {
            logger.error(e) { "Error reading configuration file" }
            throw e
        }
    }
}