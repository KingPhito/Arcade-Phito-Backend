package com.ralphdugue.arcadephitogrpc.domain.appusers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.appusers.AppUserRepository
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.RetrieveAppUserParams
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.UserAccount
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

class RetrieveAppUser(
    private val appUserRepository: AppUserRepository,
    private val logger: KLogger = KotlinLogging.logger {}
) : CoroutinesUseCase<RetrieveAppUserParams, UserAccount?> {
    override suspend fun execute(param: RetrieveAppUserParams): UserAccount? {
        return try {
            appUserRepository.getUserAccount(param.username)!!
        } catch (e: Exception) {
            logger.debug(e) { "Error retrieving user." }
            null
        }
    }

}