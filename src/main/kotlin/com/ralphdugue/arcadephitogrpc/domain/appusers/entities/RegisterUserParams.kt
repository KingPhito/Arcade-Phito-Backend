package com.ralphdugue.arcadephitogrpc.domain.appusers.entities

import com.ralphdugue.arcadephitogrpc.domain.Entity
import java.time.LocalDate

data class RegisterUserParams(
    val username: String,
    val email: String,
    val birthdate: LocalDate,
    val password: String,
): Entity
