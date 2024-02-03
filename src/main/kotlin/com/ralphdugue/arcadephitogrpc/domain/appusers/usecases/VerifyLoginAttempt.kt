package com.ralphdugue.arcadephitogrpc.domain.appusers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.appusers.AppUserRepository
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.LoginAttemptParams
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.UserAccount
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VerifyLoginAttempt(
    private val appUserRepository: AppUserRepository,
    private val securityRepository: SecurityRepository,
    private val logger: KLogger = KotlinLogging.logger {}
): CoroutinesUseCase<LoginAttemptParams, Pair<Boolean, UserAccount?>> {
    override suspend fun execute(param: LoginAttemptParams): Pair<Boolean, UserAccount?> {
        val userAccount = try {
            appUserRepository.getUserAccount(param.username)
        } catch (e: Exception) {
            logger.debug(e) { "Error retrieving user account." }
            null
        }
        return if (userAccount != null) {
            val validPassword = securityRepository.verifyHash(param.password, userAccount.passwordHash)
            Pair(validPassword, userAccount)
        } else {
            Pair(false, null)
        }
    }
}
