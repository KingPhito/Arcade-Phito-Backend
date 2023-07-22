package com.ralphdugue.arcadephito.backend.domain.usecase

import com.ralphdugue.arcadephito.backend.domain.entities.RegistrationFields
import com.ralphdugue.arcadephito.backend.domain.entities.User
import com.ralphdugue.arcadephito.backend.domain.repositories.UserRepository

class RegisterUser(private val userRepository: UserRepository): UseCase<RegistrationFields, User> {

    override suspend fun execute(param: RegistrationFields): User {
        val user = userRepository.addUser(param.username, param.email, param.password)
        return User(email = user.email, username = user.username)
    }
}