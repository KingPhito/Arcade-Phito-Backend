package com.ralphdugue.arcadephitogrpc.services

import com.ralphdugue.arcadephitogrpc.domain.developers.entities.CreateDeveloperParams
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.GenerateDeveloperTokenParams
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.ValidateDeveloperParams
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.CreateDeveloper
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.GenerateDeveloperToken
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.ValidateDeveloper
import developer.Developer
import developer.DeveloperServiceGrpcKt
import kotlin.random.Random

class DeveloperService(
    private val createDeveloper: CreateDeveloper,
    private val validateDeveloper: ValidateDeveloper,
    private val generateDeveloperToken: GenerateDeveloperToken
) :

    DeveloperServiceGrpcKt.DeveloperServiceCoroutineImplBase() {

    override suspend fun createDeveloper(request: Developer.CreateDeveloperRequest): Developer.CreateDeveloperResponse {
        val apiKey = request.developerId + generateRandomString()
        val apiSecret = generateRandomString()
        val params = CreateDeveloperParams(
            devId = request.developerId,
            email = request.email,
            apiKey = apiKey,
            apiSecret = apiSecret
        )
        return if (createDeveloper.execute(params)) {
            val builder = Developer.CreateDeveloperResponse.newBuilder()
                .setApiKey(apiKey)
                .setApiSecret(apiSecret)
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
                .setToken(generateDeveloperToken.execute(GenerateDeveloperTokenParams(request.developerId)))

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

    private fun generateRandomString(): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..32)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}