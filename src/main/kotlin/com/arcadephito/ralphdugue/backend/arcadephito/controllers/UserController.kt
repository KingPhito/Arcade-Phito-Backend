package com.arcadephito.ralphdugue.backend.arcadephito.controllers

import com.arcadephito.ralphdugue.backend.arcadephito.data.models.User

interface UserController {

    suspend fun create(user: User): Int

    suspend fun read(username: String): User?

    suspend fun update(username: String, user: User)

    suspend fun delete(username: String)
}