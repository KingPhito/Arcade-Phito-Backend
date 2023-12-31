package com.ralphdugue.arcadephitogrpc.domain.developers.usecases

import com.auth0.jwt.JWT
import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.VerifyDeveloperTokenParams
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VerifyDeveloperToken(
    private val config: ArcadePhitoConfig,
    private val developerRepository: DeveloperRepository,
    private val securityRepository: SecurityRepository,
    private val logger: KLogger = KotlinLogging.logger {}
) : CoroutinesUseCase<VerifyDeveloperTokenParams, Boolean> {
    override suspend fun execute(param: VerifyDeveloperTokenParams): Boolean {
        return try {
            val verifier = JWT.decode(param.token)
            val account = developerRepository.getDeveloperCredentials(verifier.getClaim("devId").asString())
            account?.let {
                withContext(Dispatchers.Default) {
                    val hasAudience =verifier.audience.contains(config.jwt.audience)
                    val hasIssuer = verifier.issuer == config.jwt.issuer
                    val verifiedKey = securityRepository.verifyHash(
                                hash = account.apiKeyHash,
                                data = verifier.getClaim("apiKey").asString()
                            )
                    val verifiedSecret = securityRepository.verifyHash(
                                hash = account.apiSecretHash,
                                data = verifier.getClaim("apiSecret").asString()
                            )
                    hasAudience && hasIssuer && verifiedKey && verifiedSecret
                }
            } ?: false
        } catch (e: Exception) {
            logger.debug(e) { "Error verifying developer token." }
            false
        }
    }
}
