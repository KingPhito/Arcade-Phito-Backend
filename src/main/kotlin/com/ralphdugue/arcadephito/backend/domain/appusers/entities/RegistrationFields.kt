package com.ralphdugue.arcadephito.backend.domain.appusers.entities

import com.ralphdugue.arcadephito.backend.domain.Entity

data class RegistrationFields(val email: String, val username: String, val password: String): Entity