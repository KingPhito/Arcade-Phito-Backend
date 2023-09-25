package com.ralphdugue.arcadephitogrpc.adapters.developers

import com.ralphdugue.arcadephitogrpc.Api_credentials
import com.ralphdugue.arcadephitogrpc.ArcadePhito
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.DeveloperAccount

class DeveloperRepositoryImpl(private val arcadePhitoDB: ArcadePhito) : DeveloperRepository {
    override suspend fun getDeveloperCredentials(devId: String): DeveloperAccount? {
        return arcadePhitoDB.developersQueries
            .getApiUser(devId)
            .executeAsOneOrNull()
            ?.let {
                DeveloperAccount(
                    devId = it.developer_id,
                    email = it.email,
                    apiKeyHash = it.api_key_hash,
                    apiSecretHash = it.api_secret_hash
                )
            }
    }

    override suspend fun addDeveloperCredentials(
        devId: String,
        email: String,
        apiKeyHash: String,
        apiSecretHash: String
    ): Boolean {
        return arcadePhitoDB.developersQueries.createApiUser(
            developer_id = devId,
            email = email,
            api_key_hash = apiKeyHash,
            api_secret_hash = apiSecretHash
        ).executeAsOneOrNull()?.let { it == devId } ?: false
    }
}