package com.ralphdugue.arcadephitogrpc.domain.admin.usecase

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.admin.entities.AdminAccount
import com.ralphdugue.arcadephitogrpc.domain.admin.entities.CreateAdminParams
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

class CreateAdmin(
    private val developerRepository: DeveloperRepository,
    private val securityRepository: SecurityRepository,
    private val logger: KLogger = KotlinLogging.logger {}
) : CoroutinesUseCase<CreateAdminParams, Boolean> {

    override suspend fun execute(param: CreateAdminParams): Boolean {
        return try {
            val hashedKey = securityRepository.hashData(param.apiKey)
            val hashedSecret = securityRepository.hashData(param.apiSecret)
            val adminAccount = AdminAccount(
                devId = param.devId,
                email = param.email,
                apiKeyHash = hashedKey ?: "",
                apiSecretHash = hashedSecret ?: ""
            )
            developerRepository.addDeveloperCredentials(
                devId = adminAccount.devId,
                email = adminAccount.email,
                apiKeyHash = adminAccount.apiKeyHash,
                apiSecretHash = adminAccount.apiSecretHash,
                isAdmin = true
            )
            true
        } catch (e: Exception) {
            logger.debug(e) { "Error creating admin." }
            false
        }
    }
}