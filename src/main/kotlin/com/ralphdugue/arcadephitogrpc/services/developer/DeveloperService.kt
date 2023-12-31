package com.ralphdugue.arcadephitogrpc.services.developer

import com.google.rpc.Status
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.DeveloperEntities
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.GenerateDeveloperTokenParams
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.ValidateDeveloperParams
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.CreateDeveloper
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.GenerateDeveloperToken
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.ValidateDeveloper
import developer.Developer
import developer.DeveloperServiceGrpcKt
import developer.authResponse
import developer.createDeveloperResponse

class DeveloperService(
    private val createDeveloper: CreateDeveloper,
    private val validateDeveloper: ValidateDeveloper,
    private val generateDeveloperToken: GenerateDeveloperToken
) : DeveloperServiceGrpcKt.DeveloperServiceCoroutineImplBase() {

    override suspend fun createDeveloper(request: Developer.CreateDeveloperRequest): Developer.CreateDeveloperResponse {
        val params = DeveloperEntities(
            devId = request.developerId,
            email = request.email,
        )
        val (success, response) = createDeveloper.execute(params)
        return if (success) {
            createDeveloperResponse {
                apiKey = response?.apiKey ?: ""
                apiSecret = response?.apiSecret ?: ""
                developerId = response?.devId ?: ""
                status = Status.newBuilder()
                    .setCode(200)
                    .setMessage("Developer created successfully")
                    .build()
            }
        } else {
            createDeveloperResponse {
                apiKey = ""
                apiSecret = ""
                developerId = ""
                status = Status.newBuilder()
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
                    status = Status.newBuilder()
                        .setCode(200)
                        .setMessage("Developer authenticated successfully")
                        .build()
                }
            } else {
                authResponse {
                    token = ""
                    status = Status.newBuilder()
                        .setCode(401)
                        .setMessage("Developer could not be authenticated")
                        .build()
                }
            }
        } else {
            authResponse {
                token = ""
                status = Status.newBuilder()
                    .setCode(400)
                    .setMessage("Developer could not be authenticated")
                    .build()
            }
        }
    }
}