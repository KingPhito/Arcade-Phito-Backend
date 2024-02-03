package com.ralphdugue.arcadephitogrpc.domain.developers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.ValidateDeveloperParams
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class VerifyDeveloperCredentials(
    private val developerRepository: DeveloperRepository,
    private val securityRepository: SecurityRepository,
    private val logger: KLogger = KotlinLogging.logger {}
) : CoroutinesUseCase<ValidateDeveloperParams, Boolean> {

    override suspend fun execute(param: ValidateDeveloperParams): Boolean = coroutineScope {
        try {
            val developerAccount = developerRepository.getDeveloperCredentials(param.devId)
            val validKey = async {
                securityRepository.verifyHash(
                    data = param.apiKey,
                    hash = developerAccount?.apiKeyHash ?: ""
                )
            }
            val validSecret = async {
                securityRepository.verifyHash(
                    data = param.apiSecret,
                    hash = developerAccount?.apiSecretHash ?: ""
                )
            }

            logger.info { "Validation for API key and secret were $validKey and $validSecret respectively." }
            validKey.await() && validSecret.await()
        } catch (e: Exception) {
            logger.debug(e) { "Error validating developer." }
            false
        }
    }
}
