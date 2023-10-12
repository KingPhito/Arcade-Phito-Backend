package com.ralphdugue.arcadephitogrpc.adapters.appusers

import com.ralphdugue.arcadephitogrpc.ArcadePhitoDB
import com.ralphdugue.arcadephitogrpc.domain.appusers.AppUserRepository
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.UserAccount
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDate

class AppUserRepositoryImpl(
    private val arcadePhitoDB: ArcadePhitoDB,
    private val logger: KLogger = KotlinLogging.logger {}
) : AppUserRepository {

    override suspend fun getUserAccount(username: String): UserAccount? {
        return try {
            arcadePhitoDB.databaseQueries.selectUser(username)
                .executeAsOneOrNull()?.let { user ->
                    UserAccount(
                        username = user.name,
                        passwordHash = user.password_hash,
                        email = user.email,
                        birthdate = user.date_of_birth.toString()
                    )
                }
        } catch (e: Exception) {
            logger.debug(e) { "Error retrieving user account." }
            null
        }
    }

    override suspend fun addUserAccount(
        username: String,
        email: String,
        birthdate: String,
        passwordHash: String
    ): Boolean {
        return try {
            arcadePhitoDB.databaseQueries.insertUser(
                name = username,
                email = email,
                date_of_birth = LocalDate.parse(birthdate),
                password_hash = passwordHash
            ).executeAsOneOrNull() != null
        } catch (e: Exception) {
            logger.debug(e) { "Error adding user account." }
            false
        }
    }
}