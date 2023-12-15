package com.ralphdugue.arcadephitogrpc.domain.admin.usecase

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.admin.entities.ValidateAdminParams
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ValidateAdmin(
    private val developerRepository: DeveloperRepository,
    private val securityRepository: SecurityRepository,
    private val logger: KLogger = KotlinLogging.logger {}
) : CoroutinesUseCase<ValidateAdminParams, Boolean> {

    override suspend fun execute(param: ValidateAdminParams): Boolean {
        return try {
            val adminAccount = developerRepository.getDeveloperCredentials(param.devId)
            val validKey = withContext(Dispatchers.Default) {
                securityRepository.verifyHash(
                    data = param.apiKey,
                    hash = adminAccount?.apiKeyHash ?: ""
                )
            }
            val validSecret = withContext(Dispatchers.Default) {
                securityRepository.verifyHash(
                    data = param.apiSecret,
                    hash = adminAccount?.apiSecretHash ?: ""
                )
            }
            logger.info { "Validation for API key and secret were $validKey and $validSecret respectively." }
            validKey && validSecret
        } catch (e: Exception) {
            logger.debug(e) { "Error validating admin." }
            false
        }
    }
}