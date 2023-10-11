package com.ralphdugue.arcadephitogrpc.domain.appusers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.appusers.AppUserRepository
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.LoginAttemptParams
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository

class VerifyLoginAttempt(
    private val appUserRepository: AppUserRepository,
    private val securityRepository: SecurityRepository
): CoroutinesUseCase<LoginAttemptParams, Boolean> {
    override suspend fun execute(params: LoginAttemptParams): Boolean {
        val userAccount = appUserRepository.getUserAccount(params.username)
        return userAccount?.let {
            userAccount.username == params.username &&
            securityRepository.verifyHash(params.password, userAccount.passwordHash)
        } ?: false
    }
}
