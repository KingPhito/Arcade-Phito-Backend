package com.ralphdugue.arcadephitogrpc.domain.developers.entities

import com.ralphdugue.arcadephitogrpc.domain.Entity

data class ValidateDeveloperParams(
    val devId: String,
    val apiKey: String,
    val apiSecret: String,
): Entity
