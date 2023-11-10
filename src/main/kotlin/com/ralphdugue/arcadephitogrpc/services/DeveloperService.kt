package com.ralphdugue.arcadephitogrpc.services

import com.google.rpc.Status
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.CreateDeveloperParams
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.GenerateDeveloperTokenParams
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.ValidateDeveloperParams
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.VerifyDeveloperTokenParams
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.CreateDeveloper
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.GenerateDeveloperToken
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.ValidateDeveloper
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.VerifyDeveloperToken
import developer.Developer
import developer.DeveloperServiceGrpcKt
import developer.authResponse
import developer.createDeveloperResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeveloperService(
    private val createDeveloper: CreateDeveloper,
    private val validateDeveloper: ValidateDeveloper,
    private val generateDeveloperToken: GenerateDeveloperToken
) : DeveloperServiceGrpcKt.DeveloperServiceCoroutineImplBase() {

    override suspend fun createDeveloper(request: Developer.CreateDeveloperRequest): Developer.CreateDeveloperResponse {
        val params = CreateDeveloperParams(
            devId = request.developerId,
            email = request.email,
        )
        val (success, response) = createDeveloper.execute(params)
        return if (success) {
            createDeveloperResponse {
                apiKey = response?.apiKey ?: ""
                apiSecret = response?.apiSecret ?: ""
                developerId = response?.devId ?: ""
                result = Status.newBuilder()
                    .setCode(200)
                    .setMessage("Developer created successfully")
                    .build()
            }
        } else {
            createDeveloperResponse {
                apiKey = ""
                apiSecret = ""
                developerId = ""
                result = Status.newBuilder()
                    .setCode(402)
                    .setMessage("Developer could not be created")
                    .build()

            }
        }
    }

    override suspend fun authenticateDeveloper(request: Developer.AuthRequest): Developer.AuthResponse {
        val params = ValidateDeveloperParams(
            devId = request.developerId,
            apiKey = request.apiKey,
            apiSecret = request.apiSecret
        )
        return if (validateDeveloper.execute(params)) {
            val generatedToken = generateDeveloperToken.execute(
                GenerateDeveloperTokenParams(
                    devId = request.developerId,
                    apiKey = request.apiKey,
                    apiSecret = request.apiSecret
                )
            )
            if (!generatedToken.isNullOrBlank()) {
                authResponse {
                    token = generatedToken
                    result = Status.newBuilder()
                        .setCode(200)
                        .setMessage("Developer authenticated successfully")
                        .build()
                }
            } else {
                authResponse {
                    token = ""
                    result = Status.newBuilder()
                        .setCode(401)
                        .setMessage("Developer could not be authenticated")
                        .build()
                }
            }
        } else {
            authResponse {
                token = ""
                result = Status.newBuilder()
                    .setCode(400)
                    .setMessage("Developer could not be authenticated")
                    .build()
            }
        }
    }
}