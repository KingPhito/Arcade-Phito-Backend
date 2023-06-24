package com.ralphdugue.backend.arcadephito.data.repositories

import com.ralphdugue.backend.arcadephito.data.models.UserTableRow
import com.ralphdugue.backend.arcadephito.domain.LoginFields
import com.ralphdugue.backend.arcadephito.domain.UserResponse

interface UserRepository {

    suspend fun createNewUser(user: UserTableRow): UserResponse

    suspend fun getUserFromLoginFields(fields: LoginFields): UserResponse?

    suspend fun getUserByUsername(username: String): UserResponse?

    suspend fun updateUser(username: String, user: UserTableRow)

    suspend fun deleteUser(username: String)
}