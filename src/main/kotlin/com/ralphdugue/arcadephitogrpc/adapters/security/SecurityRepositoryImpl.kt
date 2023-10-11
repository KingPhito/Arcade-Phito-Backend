package com.ralphdugue.arcadephitogrpc.adapters.security

import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class SecurityRepositoryImpl(private val encoder: BCryptPasswordEncoder) : SecurityRepository {
    override fun hashData(data: String): String = encoder.encode(data)

    override fun verifyHash(data: String, hash: String): Boolean = encoder.matches(data, hash)
}