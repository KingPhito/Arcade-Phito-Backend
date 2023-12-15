package com.ralphdugue.arcadephitogrpc.domain.admin.usecase

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.admin.entities.GenerateAdminTokenParams
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

class GenerateAdminToken(
    private val arcadePhitoConfig: ArcadePhitoConfig,
    private val logger: KLogger = KotlinLogging.logger {}
) : CoroutinesUseCase<GenerateAdminTokenParams, String?> {
    override suspend fun execute(param: GenerateAdminTokenParams): String? = try {
        val jwt = JWT.create()
            .withAudience(arcadePhitoConfig.jwt.audience)
            .withIssuer(arcadePhitoConfig.jwt.issuer)
            .withClaim("adminId", param.devId)
            .withClaim("apiKey", param.apiKey)
            .withClaim("apiSecret", param.apiSecret)
            .sign(Algorithm.HMAC256(arcadePhitoConfig.jwt.secret))
        logger.info { "Admin token generated." }
        jwt
    } catch (e: Exception) {
        logger.debug(e) { "Error generating admin token." }
        null
    }
}