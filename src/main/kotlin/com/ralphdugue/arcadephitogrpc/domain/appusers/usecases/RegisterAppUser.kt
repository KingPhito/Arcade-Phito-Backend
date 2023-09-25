package com.ralphdugue.arcadephitogrpc.domain.appusers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.appusers.AppUserRepository
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.RegisterUserParams
import org.mindrot.jbcrypt.BCrypt

class RegisterAppUser(private val appUserRepository: AppUserRepository)
    : CoroutinesUseCase<RegisterUserParams, Boolean> {

    override suspend fun execute(params: RegisterUserParams): Boolean {
         return appUserRepository.addUserAccount(
             username = params.username,
             email = params.email,
             birthdate = params.birthdate.toString(),
             passwordHash = BCrypt.hashpw(params.password, BCrypt.gensalt())
         )
    }
}