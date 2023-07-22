package com.ralphdugue.arcadephito.backend.domain.usecase

import com.ralphdugue.arcadephito.backend.domain.entities.LoginFields
import com.ralphdugue.arcadephito.backend.domain.entities.User
import com.ralphdugue.arcadephito.backend.domain.repositories.UserRepository
import org.mindrot.jbcrypt.BCrypt

class LoginUser(private val userRepository: UserRepository): UseCase<LoginFields, User> {

    override suspend fun execute(param: LoginFields): User {
        userRepository.getUserByUsername(param.username)?.let { row ->
            if (BCrypt.checkpw(param.password, row.passwordHash)) {
                return User(email = row.email, username = row.username)
            } else throw Exception("Invalid credentials")
        } ?: run { throw Exception("User not found") }
    }
}