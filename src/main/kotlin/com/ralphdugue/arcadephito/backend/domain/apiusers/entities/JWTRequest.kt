package com.ralphdugue.arcadephito.backend.domain.apiusers.entities

import com.ralphdugue.arcadephito.backend.domain.Entity
import kotlinx.serialization.Serializable

@Serializable
data class JWTRequest(
    val entityId: String,
    val apiKey: String,
    val apiSecret: String
): Entity
