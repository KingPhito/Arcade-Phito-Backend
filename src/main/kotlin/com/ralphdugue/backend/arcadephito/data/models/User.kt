package com.ralphdugue.backend.arcadephito.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val email: String,
    val passwordHash: String
)