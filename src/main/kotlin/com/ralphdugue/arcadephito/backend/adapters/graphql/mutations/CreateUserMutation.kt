package com.ralphdugue.arcadephito.backend.adapters.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.ralphdugue.arcadephito.backend.domain.appusers.entities.RegistrationFields
import com.ralphdugue.arcadephito.backend.domain.appusers.usecase.RegisterUser

class CreateUserMutation(private val registerUser: RegisterUser) : Mutation {

        suspend fun registerUser(username: String, email: String, password: String) =
            registerUser.execute(RegistrationFields(username, email, password))
}