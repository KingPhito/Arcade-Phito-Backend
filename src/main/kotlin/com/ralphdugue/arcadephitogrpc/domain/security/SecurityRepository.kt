package com.ralphdugue.arcadephitogrpc.domain.security

interface SecurityRepository {

    suspend fun hashData(data: String): String?

    suspend fun verifyHash(data: String, hash: String): Boolean
}