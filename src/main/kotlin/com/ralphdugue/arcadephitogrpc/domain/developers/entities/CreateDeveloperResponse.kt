package com.ralphdugue.arcadephitogrpc.domain.developers.entities

import com.ralphdugue.arcadephitogrpc.domain.Entity

data class CreateDeveloperResponse(
    val devId: String,
    val email: String,
    val apiKey: String,
    val apiSecret: String,
): Entity
