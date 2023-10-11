package com.ralphdugue.arcadephitogrpc.adapters.developers

import com.ralphdugue.arcadephitogrpc.ArcadePhitoDB
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.DeveloperAccount

class DeveloperRepositoryImpl(private val arcadePhitoDB: ArcadePhitoDB) : DeveloperRepository {
    override suspend fun getDeveloperCredentials(devId: String): DeveloperAccount? {
        val row = arcadePhitoDB.databaseQueries
            .getApiUser(devId)
            .executeAsOneOrNull()
        return if (row != null) {
            DeveloperAccount(
                devId = row.developer_id,
                email = row.email,
                apiKeyHash = row.api_key_hash,
                apiSecretHash = row.api_secret_hash
            )
        } else {
            null
        }
    }

    override suspend fun addDeveloperCredentials(
        devId: String,
        email: String,
        apiKeyHash: String,
        apiSecretHash: String
    ): Boolean {
        val createdDev = arcadePhitoDB.databaseQueries.createApiUser(
            developer_id = devId,
            email = email,
            api_key_hash = apiKeyHash,
            api_secret_hash = apiSecretHash
        ).executeAsOneOrNull()
        return if (createdDev != null) {
            createdDev == devId
        } else {
            false
        }
    }
}