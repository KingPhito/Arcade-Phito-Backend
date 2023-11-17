package com.ralphdugue.arcadephitogrpc.domain.appusers.usecases

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.GenerateUserTokenParams
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

class GenerateUserToken(
    private val config: ArcadePhitoConfig,
    private val logger: KLogger = KotlinLogging.logger {}
) : CoroutinesUseCase<GenerateUserTokenParams, String?> {
    override suspend fun execute(param: GenerateUserTokenParams): String? = try {
        val jwt = JWT.create()
            .withAudience(config.jwt.audience)
            .withIssuer(config.jwt.issuer)
            .withClaim("username", param.username)
            .withClaim("password", param.password)
            .sign(Algorithm.HMAC256(config.jwt.secret))
        logger.info { "User token generated." }
        jwt
    } catch (e: Exception) {
        logger.debug(e) { "Error generating user token." }
        null
    }
}