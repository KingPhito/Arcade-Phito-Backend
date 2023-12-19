package com.ralphdugue.arcadephitogrpc.domain.admin.usecase

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.admin.entities.AdminAccount
import com.ralphdugue.arcadephitogrpc.domain.admin.entities.CreateAdminParams
import com.ralphdugue.arcadephitogrpc.domain.admin.entities.CreateAdminResponse
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import java.security.SecureRandom
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class CreateAdmin(
    private val developerRepository: DeveloperRepository,
    private val securityRepository: SecurityRepository,
    private val logger: KLogger = KotlinLogging.logger {}
) : CoroutinesUseCase<CreateAdminParams, Pair<Boolean, CreateAdminResponse?>> {

    override suspend fun execute(param: CreateAdminParams): Pair<Boolean, CreateAdminResponse?> {
        return try {
            val apiKey = generateApiCred(param.devId)
            val apiSecret = generateApiCred()
            val hashedKey = securityRepository.hashData(apiKey)
            val hashedSecret = securityRepository.hashData(apiSecret)
            val adminAccount = AdminAccount(
                devId = param.devId,
                email = param.email,
                apiKeyHash = hashedKey ?: "",
                apiSecretHash = hashedSecret ?: ""
            )
            developerRepository.addDeveloperCredentials(
                devId = adminAccount.devId,
                email = adminAccount.email,
                apiKeyHash = adminAccount.apiKeyHash,
                apiSecretHash = adminAccount.apiSecretHash,
                isAdmin = true
            )
            Pair(true, CreateAdminResponse(
                devId = adminAccount.devId,
                email = adminAccount.email,
                apiKey = apiKey,
                apiSecret = apiSecret
            ))
        } catch (e: Exception) {
            logger.debug(e) { "Error creating admin." }
            Pair(false, null)
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun generateApiCred(name: String = ""): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)

        return name.lowercase() + Base64.encode(bytes)
    }
}