package com.ralphdugue.arcadephitogrpc.domain.developers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.CreateDeveloperParams
import org.mindrot.jbcrypt.BCrypt
import kotlin.random.Random

class CreateDeveloper(private val developerRepository: DeveloperRepository)
    : CoroutinesUseCase<CreateDeveloperParams, Boolean> {
    override suspend fun execute(param: CreateDeveloperParams): Boolean {
        return developerRepository.addDeveloperCredentials(
            devId = param.devId,
            email = param.email,
            apiKeyHash = BCrypt.hashpw(param.apiKey, BCrypt.gensalt()),
            apiSecretHash = BCrypt.hashpw(param.apiSecret, BCrypt.gensalt())
        )
    }
}