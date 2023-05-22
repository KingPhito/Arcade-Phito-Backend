package com.ralphdugue.backend.arcadephito.domain

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(val username: String, val email: String)