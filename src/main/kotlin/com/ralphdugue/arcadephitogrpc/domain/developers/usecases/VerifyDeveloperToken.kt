package com.ralphdugue.arcadephitogrpc.domain.developers.usecases

import com.auth0.jwt.JWT
import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.VerifyDeveloperTokenParams
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository

class VerifyDeveloperToken(
    private val config: ArcadePhitoConfig,
    private val developerRepository: DeveloperRepository,
    private val securityRepository: SecurityRepository
) : CoroutinesUseCase<VerifyDeveloperTokenParams, Boolean> {
    override suspend fun execute(param: VerifyDeveloperTokenParams): Boolean {
        val verifier = JWT.decode(param.token)
        val account = developerRepository.getDeveloperCredentials(verifier.getClaim("devId").asString())
        return account?.let {
            verifier.audience.contains(config.jwt.audience) &&
            verifier.issuer == config.jwt.issuer &&
            securityRepository.verifyHash(account.apiKeyHash, verifier.getClaim("apiKey").asString()) &&
            securityRepository.verifyHash(account.apiSecretHash, verifier.getClaim("apiSecret").asString())
        } ?: false
    }
}
