package com.ralphdugue.arcadephito.backend.domain.repositories

import com.ralphdugue.arcadephito.backend.data.database.UserTableRow
import com.ralphdugue.arcadephito.backend.domain.entities.User

interface UserRepository {

    suspend fun addUser(
        username: String,
        email: String,
        password: String,
    ): UserTableRow

    suspend fun getUserByUsername(username: String): UserTableRow?
}