package com.ralphdugue.arcadephitogrpc.domain.developers

import com.ralphdugue.arcadephitogrpc.domain.developers.entities.DeveloperAccount

interface DeveloperRepository {

    suspend fun getDeveloperCredentials(devId: String): DeveloperAccount?

    suspend fun addDeveloperCredentials(
        devId: String,
        email: String,
        apiKeyHash: String,
        apiSecretHash: String,
    ): Boolean
}