package com.ralphdugue.arcadephitogrpc.domain.developers.entities

import com.ralphdugue.arcadephitogrpc.domain.Entity

data class DeveloperAccount(
    val devId: String,
    val email: String,
    val apiKeyHash: String,
    val apiSecretHash: String
): Entity
