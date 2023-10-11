package com.ralphdugue.arcadephitogrpc.domain.developers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.ValidateDeveloperParams
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository

class ValidateDeveloper(
    private val developerRepository: DeveloperRepository,
    private val securityRepository: SecurityRepository
) : CoroutinesUseCase<ValidateDeveloperParams, Boolean> {
    override suspend fun execute(params: ValidateDeveloperParams): Boolean {
        val developerAccount = developerRepository.getDeveloperCredentials(params.devId)
        val validKey = securityRepository.verifyHash(params.apiKey, developerAccount?.apiKeyHash ?: "")
        val validSecret = securityRepository.verifyHash(params.apiSecret, developerAccount?.apiSecretHash ?: "")
        return validKey && validSecret
    }
}
