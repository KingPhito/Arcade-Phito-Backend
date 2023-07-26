package com.ralphdugue.arcadephito.backend.adapters.graphql.queries

import com.expediagroup.graphql.server.operations.Query
import com.ralphdugue.arcadephito.backend.domain.entities.LoginFields
import com.ralphdugue.arcadephito.backend.domain.usecase.LoginUser

class LoginUserQuery(private val loginUser: LoginUser): Query {

        suspend fun loginUser(username: String, password: String) =
            loginUser.execute(LoginFields(username, password))
}