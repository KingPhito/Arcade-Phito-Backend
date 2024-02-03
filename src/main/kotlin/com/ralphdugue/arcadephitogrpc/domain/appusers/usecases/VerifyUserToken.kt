package com.ralphdugue.arcadephitogrpc.domain.appusers.usecases

import com.auth0.jwt.JWT
import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.appusers.AppUserRepository
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.VerifyUserTokenParams
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VerifyUserToken(
    private val config: ArcadePhitoConfig,
    private val appUserRepository: AppUserRepository,
    private val securityRepository: SecurityRepository,
    private val logger: KLogger = KotlinLogging.logger {}
) : CoroutinesUseCase<VerifyUserTokenParams, Boolean> {
    override suspend fun execute(param: VerifyUserTokenParams): Boolean {
        return try {
            val verifier = JWT.decode(param.token)
            val account = appUserRepository.getUserAccount(verifier.getClaim("username").asString())
            account?.let {
                val hasAudience = verifier.audience.contains(config.jwt.audience)
                val hasIssuer = verifier.issuer == config.jwt.issuer
                val verifiedPassword = securityRepository.verifyHash(
                    hash = account.passwordHash,
                    data = verifier.getClaim("password").asString()
                )
                hasAudience && hasIssuer && verifiedPassword
            } ?: false
        } catch (e: Exception) {
            logger.debug(e) { "Error verifying user token." }
            false
        }
    }
}