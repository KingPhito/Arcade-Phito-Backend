package com.ralphdugue.arcadephitogrpc.domain.admin.usecase

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.admin.entities.ValidateAdminParams
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*

class VerifyAdminCredentials(
    private val developerRepository: DeveloperRepository,
    private val securityRepository: SecurityRepository,
    private val logger: KLogger = KotlinLogging.logger {}
) : CoroutinesUseCase<ValidateAdminParams, Boolean> {

    override suspend fun execute(param: ValidateAdminParams): Boolean = coroutineScope {
        try {
            val adminAccount = developerRepository.getDeveloperCredentials(param.devId)
            val validKey = async {
                securityRepository.verifyHash(
                    data = param.apiKey,
                    hash = adminAccount?.apiKeyHash ?: ""
                )
            }
            val validSecret = async {
                securityRepository.verifyHash(
                    data = param.apiSecret,
                    hash = adminAccount?.apiSecretHash ?: ""
                )
            }

            logger.info { "Validation for API key and secret were $validKey and $validSecret respectively." }
            validKey.await() && validSecret.await()
        } catch (e: Exception) {
            logger.debug(e) { "Error validating admin." }
            false
        }
    }
}