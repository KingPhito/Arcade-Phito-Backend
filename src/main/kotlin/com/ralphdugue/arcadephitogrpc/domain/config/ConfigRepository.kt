package com.ralphdugue.arcadephitogrpc.domain.config

import com.google.cloud.secretmanager.v1beta1.SecretManagerServiceClient
import com.ralphdugue.arcadephitogrpc.domain.config.entities.AdminConfig
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import com.ralphdugue.arcadephitogrpc.domain.config.entities.DatabaseConfig
import com.ralphdugue.arcadephitogrpc.domain.config.entities.JWTConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import java.io.File

interface ConfigRepository {

    suspend fun initDatabase()
}