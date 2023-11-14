package com.ralphdugue.arcadephitogrpc.adapters.security

import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class SecurityRepositoryImpl(
    private val encoder: BCryptPasswordEncoder,
    private val logger: KLogger = KotlinLogging.logger {}
) : SecurityRepository {
    override suspend fun hashData(data: String): String? = try {
        encoder.encode(data)
    } catch (e: Exception) {
        logger.debug(e) { "Error hashing data." }
        null
    }

    override suspend fun verifyHash(data: String, hash: String): Boolean = try {
        encoder.matches(data, hash)
    } catch (e: Exception) {
        logger.debug(e) { "Error verifying hash." }
        false
    }
}