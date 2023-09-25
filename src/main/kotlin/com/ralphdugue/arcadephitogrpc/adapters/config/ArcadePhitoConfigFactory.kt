package com.ralphdugue.arcadephitogrpc.adapters.config

import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import kotlinx.serialization.json.Json
import java.io.File

object ArcadePhitoConfigFactory {

    fun create(): ArcadePhitoConfig {
        val env = System.getenv("ENVIRONMENT") ?: "dev"
        val file = when (env) {
                "prod" -> "config.prod.json"
                else -> "config.dev.json"
        }
        return Json.decodeFromString<ArcadePhitoConfig>(File("src/main/resources/$file").readText())
    }
}