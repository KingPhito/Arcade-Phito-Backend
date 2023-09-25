package com.ralphdugue.arcadephitogrpc.domain.appusers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.appusers.AppUserRepository
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.LoginAttemptParams
import org.mindrot.jbcrypt.BCrypt

class VerifyLoginAttempt(private val appUserRepository: AppUserRepository)
    : CoroutinesUseCase<LoginAttemptParams, Boolean> {
    override suspend fun execute(params: LoginAttemptParams): Boolean {
        val userAccount = appUserRepository.getUserAccount(params.username)
        return userAccount?.let {
            userAccount.username == params.username &&
            BCrypt.checkpw(userAccount.passwordHash, params.password)
        } ?: false
    }
}
