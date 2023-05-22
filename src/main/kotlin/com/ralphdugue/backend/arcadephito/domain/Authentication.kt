package com.ralphdugue.backend.arcadephito.domain

import com.ralphdugue.backend.arcadephito.data.models.UserTableRow
import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class LoginFields(val username: String, val password: String)

@Serializable
data class RegistrationFields(val email: String, val username: String, val password: String) {
    fun userTableRow() = UserTableRow(
        email = email,
        username = username,
        passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
    )
}