package com.ralphdugue.backend.arcadephito.data.models

import com.ralphdugue.backend.arcadephito.domain.UserResponse

data class UserTableRow(
    val username: String,
    val email: String,
    val passwordHash: String
)