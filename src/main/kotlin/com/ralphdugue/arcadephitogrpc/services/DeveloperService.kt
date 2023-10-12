package com.ralphdugue.arcadephitogrpc.services

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

class DeveloperService(
    private val createDeveloper: CreateDeveloper,
    private val validateDeveloper: ValidateDeveloper,
    private val generateDeveloperToken: GenerateDeveloperToken,
    private val verifyDeveloperToken: VerifyDeveloperToken
) : DeveloperServiceGrpcKt.DeveloperServiceCoroutineImplBase() {

    override suspend fun createDeveloper(request: Developer.CreateDeveloperRequest): Developer.CreateDeveloperResponse {
        val validToken = verifyDeveloperToken.execute(VerifyDeveloperTokenParams(token = request.token))
        if (!validToken) {
            val builder = Developer.CreateDeveloperResponse.newBuilder()
                .setApiKey("")
                .setApiSecret("")
                .setDeveloperId("")

            return builder
                .setResult(
                    builder.resultBuilder
                        .setCode(401)
                        .setMessage("Unauthorized")
                        .build()
                )
                .build()
        }
        val params = CreateDeveloperParams(
            devId = request.developerId,
            email = request.email,
        )
        val (success, response) = createDeveloper.execute(params)
        return if (success) {
            val builder = Developer.CreateDeveloperResponse.newBuilder()
                .setApiKey(response?.apiKey ?: "")
                .setApiSecret(response?.apiSecret ?: "")
                .setDeveloperId(request.developerId)

            builder
                .setResult(
                    builder.resultBuilder
                        .setCode(200)
                        .setMessage("Developer created successfully")
                        .build()
                )
                .build()
        } else {
            val builder = Developer.CreateDeveloperResponse.newBuilder()
                .setApiKey("")
                .setApiSecret("")
                .setDeveloperId("")

            builder
                .setResult(
                    builder.resultBuilder
                        .setCode(400)
                        .setMessage("Developer creation failed")
                        .build()
                )
                .build()
        }
    }

    override suspend fun authenticateDeveloper(request: Developer.AuthRequest): Developer.AuthResponse {
        val params = ValidateDeveloperParams(
            devId = request.developerId,
            apiKey = request.apiKey,
            apiSecret = request.apiSecret
        )
        return if (validateDeveloper.execute(params)) {
            val builder = Developer.AuthResponse.newBuilder()
                .setToken(
                    generateDeveloperToken.execute(
                        GenerateDeveloperTokenParams(
                            devId = request.developerId,
                            apiKey = request.apiKey,
                            apiSecret = request.apiSecret
                        )
                    )
                )

            builder
                .setResult(
                    builder.resultBuilder
                        .setCode(200)
                        .setMessage("Developer authenticated successfully")
                        .build()
                )
                .build()
        } else {
            val builder = Developer.AuthResponse.newBuilder()
                .setToken("")

            builder
                .setResult(
                    builder.resultBuilder
                        .setCode(400)
                        .setMessage("Developer authentication failed")
                        .build()
                )
                .build()
        }
    }
}