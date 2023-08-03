package com.ralphdugue.arcadephito.backend.domain.appusers.usecase

import com.ralphdugue.arcadephito.backend.domain.TwoEntityUseCase
import com.ralphdugue.arcadephito.backend.domain.appusers.entities.RegistrationFields
import com.ralphdugue.arcadephito.backend.domain.appusers.entities.AppUser
import com.ralphdugue.arcadephito.backend.domain.appusers.repositories.AppUserRepository

class RegisterUser(private val appUserRepository: AppUserRepository): TwoEntityUseCase<RegistrationFields, AppUser> {

    override suspend fun execute(param: RegistrationFields): AppUser {
        val user = appUserRepository.addUser(param.username, param.email, param.password)
        return AppUser(email = user.email, username = user.username)
    }
}