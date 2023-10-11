package com.ralphdugue.arcadephitogrpc.domain.security

interface SecurityRepository {

    fun hashData(data: String): String

    fun verifyHash(data: String, hash: String): Boolean
}