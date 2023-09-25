package com.ralphdugue.arcadephitogrpc.domain.developers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.ValidateDeveloperParams
import org.mindrot.jbcrypt.BCrypt

class ValidateDeveloper(private val developerRepository: DeveloperRepository)
    : CoroutinesUseCase<ValidateDeveloperParams, Boolean> {
    override suspend fun execute(params: ValidateDeveloperParams): Boolean {
        val developerAccount = developerRepository.getDeveloperCredentials(params.devId)
        return developerAccount?.let {
            BCrypt.checkpw(developerAccount.apiKeyHash,params.apiKey) &&
            BCrypt.checkpw(developerAccount.apiSecretHash,params.apiSecret)
        } ?: false
    }
}
