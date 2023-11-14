package com.ralphdugue.arcadephitogrpc.domain.appusers.usecases

import com.ralphdugue.arcadephitogrpc.domain.CoroutinesUseCase
import com.ralphdugue.arcadephitogrpc.domain.appusers.AppUserRepository
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.RegisterUserParams
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterAppUser(
    private val appUserRepository: AppUserRepository,
    private val securityRepository: SecurityRepository,
    private val logger: KLogger = KotlinLogging.logger {}
) : CoroutinesUseCase<RegisterUserParams, Boolean> {

    override suspend fun execute(param: RegisterUserParams): Boolean {
         return try {
             appUserRepository.addUserAccount(
                 username = param.username,
                 email = param.email,
                 birthdate = param.birthdate.toString(),
                 passwordHash = withContext(Dispatchers.Default) { securityRepository.hashData(param.password) }!!,
             )
         } catch (e: Exception) {
             logger.debug(e) { "Error registering user." }
             false
         }
    }
}