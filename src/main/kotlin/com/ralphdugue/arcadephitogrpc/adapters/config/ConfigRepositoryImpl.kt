package com.ralphdugue.arcadephitogrpc.adapters.config

import app.cash.sqldelight.db.SqlDriver
import com.ralphdugue.arcadephitogrpc.ArcadePhitoDB
import com.ralphdugue.arcadephitogrpc.domain.config.ConfigRepository
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import java.sql.SQLException

class ConfigRepositoryImpl(
    private val driver: SqlDriver,
    private val developerRepository: DeveloperRepository,
    private val securityRepository: SecurityRepository,
    private val arcadePhitoConfig: ArcadePhitoConfig,
    private val logger: KLogger = KotlinLogging.logger {}
) : ConfigRepository {

    override suspend fun initDatabase() {
        try {
            ArcadePhitoDB.Schema.create(driver)
        } catch (e: SQLException) {
            logger.warn(e) { "Error creating database schema. It likely already exists." }
        } catch (e: Exception) {
            logger.error(e) { "Error creating database schema." }
            throw e
        }
        try {
            val result = developerRepository.addDeveloperCredentials(
                devId = arcadePhitoConfig.admin.developerId,
                email = arcadePhitoConfig.admin.email,
                apiKeyHash = securityRepository.hashData(arcadePhitoConfig.admin.apiKey)!!,
                apiSecretHash = securityRepository.hashData(arcadePhitoConfig.admin.apiSecret)!!
            )
            logger.info { "Attempt to create admin credentials returned $result: They likely already exist." }
        } catch (e: SQLException) {
            logger.warn(e) { "Error adding developer credentials. They likely already exists." }
        } catch (e: Exception) {
            logger.error(e) { "Error adding developer credentials." }
            throw e
        }
    }
}