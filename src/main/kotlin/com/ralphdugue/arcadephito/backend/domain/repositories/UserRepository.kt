package com.ralphdugue.arcadephito.backend.domain.repositories

import com.ralphdugue.arcadephito.backend.adapters.database.UserTableRow

interface UserRepository {

    suspend fun addUser(
        username: String,
        email: String,
        password: String,
    ): UserTableRow

    suspend fun getUserByUsername(username: String): UserTableRow?
}