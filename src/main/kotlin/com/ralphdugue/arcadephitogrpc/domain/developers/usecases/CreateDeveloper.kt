package com.ralphdugue.arcadephitogrpc.domain.developers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.DeveloperEntities
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.CreateDeveloperResponse
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class CreateDeveloper(
    private val developerRepository: DeveloperRepository,
    private val securityRepository: SecurityRepository,
    private val logger: KLogger = KotlinLogging.logger {}
) : CoroutinesUseCase<DeveloperEntities, Pair<Boolean, CreateDeveloperResponse?>>{
    override suspend fun execute(param: DeveloperEntities): Pair<Boolean, CreateDeveloperResponse?> {
        return try {
            val apiKey = generateApiCred(param.devId)
            val apiSecret = generateApiCred()
            val apiKeyHash = withContext(Dispatchers.Default) { securityRepository.hashData(apiKey) }
            val apiSecretHash = withContext(Dispatchers.Default) { securityRepository.hashData(apiSecret) }
            val success = developerRepository.addDeveloperCredentials(
                devId = param.devId,
                email = param.email,
                apiKeyHash = apiKeyHash!!,
                apiSecretHash = apiSecretHash!!
            )
            if (success) {
                Pair(true, CreateDeveloperResponse(
                    devId = param.devId,
                    email = param.email,
                    apiKey = apiKey,
                    apiSecret = apiSecret
                ))
            } else {
                Pair(false, null)
            }
        } catch (e: Exception) {
            logger.debug(e) { "Error creating developer account." }
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