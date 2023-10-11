package com.ralphdugue.arcadephitogrpc.domain.developers.usecases

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.GenerateDeveloperTokenParams

class GenerateDeveloperToken(private val config: ArcadePhitoConfig) : CoroutinesUseCase<GenerateDeveloperTokenParams, String> {
    override suspend fun execute(param: GenerateDeveloperTokenParams): String = JWT.create()
        .withAudience(config.jwt.audience)
        .withIssuer(config.jwt.issuer)
        .withClaim("devId", param.devId)
        .withClaim("apiKey", param.apiKey)
        .withClaim("apiSecret", param.apiSecret)
        .sign(Algorithm.HMAC256(config.jwt.secret))
}