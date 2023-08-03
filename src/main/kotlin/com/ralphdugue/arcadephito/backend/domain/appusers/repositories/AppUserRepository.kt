package com.ralphdugue.arcadephito.backend.domain.appusers.repositories

import com.ralphdugue.arcadephito.backend.adapters.database.AppUserTableRow

interface AppUserRepository {

    suspend fun addUser(
        username: String,
        email: String,
        password: String,
    ): AppUserTableRow

    suspend fun getUserByUsername(username: String): AppUserTableRow?
}