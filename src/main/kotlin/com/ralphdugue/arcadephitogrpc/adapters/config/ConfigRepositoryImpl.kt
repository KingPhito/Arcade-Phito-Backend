package com.ralphdugue.arcadephitogrpc.adapters.config

import app.cash.sqldelight.db.SqlDriver
import com.ralphdugue.arcadephitogrpc.ArcadePhitoDB
import com.ralphdugue.arcadephitogrpc.domain.config.ConfigRepository
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import java.sql.SQLException

class ConfigRepositoryImpl(
    private val driver: SqlDriver,
    private val arcadePhitoConfig: ArcadePhitoConfig,
    private val developerRepository: DeveloperRepository,
    private val securityRepository: SecurityRepository
) : ConfigRepository {

    override suspend fun initDatabase() {
        try {
            ArcadePhitoDB.Schema.create(driver)
        } catch (e: SQLException) {
            println("Database account already exists")
        }
        try {
            developerRepository.addDeveloperCredentials(
                devId = arcadePhitoConfig.admin.developerId,
                email = arcadePhitoConfig.admin.email,
                apiKeyHash = securityRepository.hashData(arcadePhitoConfig.admin.apiKey),
                apiSecretHash = securityRepository.hashData(arcadePhitoConfig.admin.apiSecret)
            )
        } catch (e: SQLException) {
            println("Admin account already exists")
        }
    }
}