package com.ralphdugue.backend.arcadephito.data.repositories

import com.ralphdugue.backend.arcadephito.data.models.User

interface UserRepository {

    suspend fun create(user: User): Int

    suspend fun read(username: String): User?

    suspend fun update(username: String, user: User)

    suspend fun delete(username: String)
}