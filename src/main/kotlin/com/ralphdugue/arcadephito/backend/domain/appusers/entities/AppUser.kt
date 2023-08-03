package com.ralphdugue.arcadephito.backend.domain.appusers.entities

import com.ralphdugue.arcadephito.backend.domain.Entity

data class AppUser(
    val email: String,
    val username: String
): Entity
