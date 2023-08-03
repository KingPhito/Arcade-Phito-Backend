package com.ralphdugue.arcadephito.backend.domain.appusers.usecase

import com.ralphdugue.arcadephito.backend.domain.TwoEntityUseCase
import com.ralphdugue.arcadephito.backend.domain.appusers.entities.LoginFields
import com.ralphdugue.arcadephito.backend.domain.appusers.entities.AppUser
import com.ralphdugue.arcadephito.backend.domain.appusers.repositories.AppUserRepository
import org.mindrot.jbcrypt.BCrypt

class LoginUser(private val appUserRepository: AppUserRepository): TwoEntityUseCase<LoginFields, AppUser> {

    override suspend fun execute(param: LoginFields): AppUser {
        appUserRepository.getUserByUsername(param.username)?.let { row ->
            if (BCrypt.checkpw(param.password, row.passwordHash)) {
                return AppUser(email = row.email, username = row.username)
            } else throw Exception("Invalid credentials")
        } ?: run { throw Exception("User not found") }
    }
}