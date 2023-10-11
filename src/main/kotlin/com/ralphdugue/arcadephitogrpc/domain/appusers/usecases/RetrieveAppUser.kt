package com.ralphdugue.arcadephitogrpc.domain.appusers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.appusers.AppUserRepository
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.RetrieveAppUserParams
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.UserAccount

class RetrieveAppUser(private val appUserRepository: AppUserRepository)
    : CoroutinesUseCase<RetrieveAppUserParams, UserAccount> {
    override suspend fun execute(param: RetrieveAppUserParams): UserAccount {
        return try {
            appUserRepository.getUserAccount(param.username)!!
        } catch (e: Exception) {
            throw e
        }
    }

}