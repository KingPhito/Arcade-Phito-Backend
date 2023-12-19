package com.ralphdugue.arcadephitogrpc.services.developer

import com.google.rpc.Status
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.GenerateDeveloperTokenParams
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.ValidateDeveloperParams
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.GenerateDeveloperToken
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.VerifyDeveloperCredentials
import developer.Developer
import developer.DeveloperServiceGrpcKt
import developer.authResponse

class DeveloperService(
    private val verifyDeveloperCredentials: VerifyDeveloperCredentials,
    private val generateDeveloperToken: GenerateDeveloperToken
) : DeveloperServiceGrpcKt.DeveloperServiceCoroutineImplBase() {

    override suspend fun authenticateDeveloper(request: Developer.AuthRequest): Developer.AuthResponse {
        val params = ValidateDeveloperParams(
            devId = request.developerId,
            apiKey = request.apiKey,
            apiSecret = request.apiSecret
        )
        return if (verifyDeveloperCredentials.execute(params)) {
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