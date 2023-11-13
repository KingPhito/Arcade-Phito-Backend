package com.ralphdugue.arcadephitogrpc.domain.developers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.ValidateDeveloperParams
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ValidateDeveloper(
    private val developerRepository: DeveloperRepository,
    private val securityRepository: SecurityRepository,
    private val logger: KLogger = KotlinLogging.logger {}
) : CoroutinesUseCase<ValidateDeveloperParams, Boolean> {
    override suspend fun execute(param: ValidateDeveloperParams): Boolean {
        return try {
            val developerAccount = developerRepository.getDeveloperCredentials(param.devId)
            val validKey = withContext(Dispatchers.Default) {
                securityRepository.verifyHash(
                    data = param.apiKey,
                    hash = developerAccount?.apiKeyHash ?: ""
                )
            }
            val validSecret = withContext(Dispatchers.Default) {
                securityRepository.verifyHash(
                    data = param.apiSecret,
                    hash = developerAccount?.apiSecretHash ?: ""
                )
            }
            validKey && validSecret
        } catch (e: Exception) {
            logger.debug(e) { "Error validating developer." }
            false
        }
    }
}
