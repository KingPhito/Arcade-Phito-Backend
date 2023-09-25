package com.ralphdugue.arcadephitogrpc.domain.appusers.entities

import com.ralphdugue.arcadephitogrpc.domain.Entity

data class LoginAttemptParams(
    val username: String,
    val password: String,
): Entity
