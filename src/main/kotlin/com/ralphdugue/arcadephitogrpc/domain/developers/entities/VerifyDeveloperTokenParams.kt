package com.ralphdugue.arcadephitogrpc.domain.developers.entities

import com.ralphdugue.arcadephitogrpc.domain.Entity

data class VerifyDeveloperTokenParams(
    val token: String
): Entity