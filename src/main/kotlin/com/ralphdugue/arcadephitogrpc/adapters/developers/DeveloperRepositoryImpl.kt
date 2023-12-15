package com.ralphdugue.arcadephitogrpc.adapters.developers

import com.ralphdugue.arcadephitogrpc.ArcadePhitoDB
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.DeveloperAccount
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

class DeveloperRepositoryImpl(
    private val arcadePhitoDB: ArcadePhitoDB,
    private val logger: KLogger = KotlinLogging.logger {}
) : DeveloperRepository {

    override suspend fun getDeveloperCredentials(devId: String): DeveloperAccount? {
        val row = try {
            arcadePhitoDB.databaseQueries
                .getApiUser(devId)
                .executeAsOneOrNull()
        } catch (e: Exception) {
            logger.debug(e) { "Error retrieving developer credentials for $devId." }
            null
        }
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
        apiSecretHash: String,
        isAdmin: Boolean
    ): Boolean {
        val createdDev = try {
            arcadePhitoDB.databaseQueries.createApiUser(
                developer_id = devId,
                email = email,
                api_key_hash = apiKeyHash,
                api_secret_hash = apiSecretHash,
                is_admin = isAdmin
            ).executeAsOneOrNull()
        } catch (e: Exception) {
            logger.debug(e) { "Error adding developer credentials for $devId." }
            null
        }
        return if (createdDev != null) {
            createdDev == devId
        } else {
            logger.warn { "Developer credentials for $devId likely already exist. They could not be added." }
            false
        }
    }
}