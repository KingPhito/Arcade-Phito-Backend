package com.ralphdugue.arcadephito.backend.domain.apiusers.usecase

import com.ralphdugue.arcadephito.backend.domain.EntityParamUseCase
import com.ralphdugue.arcadephito.backend.domain.apiusers.entities.JWTRequest
import com.ralphdugue.arcadephito.backend.domain.apiusers.repositories.ApiUsersRepository

class ValidateAPIUser(private val apiUsersRepository: ApiUsersRepository): EntityParamUseCase<JWTRequest, Boolean> {
    override suspend fun execute(param: JWTRequest): Boolean {
        val apiUser = apiUsersRepository.getApiUser(param.entityId)
        return apiUser?.apiKey == param.apiKey && apiUser.apiSecret == param.apiSecret
    }
}