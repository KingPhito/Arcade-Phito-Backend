package com.ralphdugue.arcadephitogrpc.domain.developers.usecases

import com.auth0.jwt.JWT
import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.VerifyDeveloperTokenParams

class VerifyDeveloperToken(private val config: ArcadePhitoConfig) : CoroutinesUseCase<VerifyDeveloperTokenParams, Boolean> {
    override suspend fun execute(param: VerifyDeveloperTokenParams): Boolean {
        val verifier = JWT.decode(param.token)
        return verifier.audience.contains(config.jwt.audience) &&
                verifier.issuer == config.jwt.issuer
    }
}
