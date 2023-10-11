package com.ralphdugue.arcadephitogrpc.domain.appusers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.appusers.AppUserRepository
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.RegisterUserParams
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository

class RegisterAppUser(
    private val appUserRepository: AppUserRepository,
    private val securityRepository: SecurityRepository
) : CoroutinesUseCase<RegisterUserParams, Boolean> {

    override suspend fun execute(params: RegisterUserParams): Boolean {
         return appUserRepository.addUserAccount(
             username = params.username,
             email = params.email,
             birthdate = params.birthdate.toString(),
             passwordHash = securityRepository.hashData(params.password)
         )
    }
}