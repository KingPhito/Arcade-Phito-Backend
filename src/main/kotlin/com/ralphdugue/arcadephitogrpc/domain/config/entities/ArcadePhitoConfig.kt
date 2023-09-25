package com.ralphdugue.arcadephitogrpc.domain.config.entities

import kotlinx.serialization.Serializable

@Serializable
data class ArcadePhitoConfig(
    val port: Int,
    val db: DatabaseConfig,
    val jwt: JWTConfig
)

@Serializable
data class DatabaseConfig(
    val host: String,
    val username: String,
    val password: String,
    val driver: String,
)

@Serializable
data class JWTConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val expiration: Long
)