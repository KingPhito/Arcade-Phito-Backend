package com.ralphdugue.arcadephitogrpc.domain.appusers

import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.UserAccount

interface AppUserRepository {

    suspend fun getUserAccount(username: String): UserAccount?

    suspend fun addUserAccount(
        username: String,
        email: String,
        birthdate: String,
        passwordHash: String,
    ): Boolean
}