package com.ralphdugue.arcadephitogrpc.adapters.appusers

import com.ralphdugue.arcadephitogrpc.ArcadePhito
import com.ralphdugue.arcadephitogrpc.domain.appusers.AppUserRepository
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.UserAccount
import java.time.LocalDate

class AppUserRepositoryImpl(private val arcadePhitoDB: ArcadePhito) : AppUserRepository {

    override suspend fun getUserAccount(username: String): UserAccount? {
        return arcadePhitoDB.usersQueries.selectUser(username)
            .executeAsOneOrNull()?.let { user ->
                UserAccount(
                    username = user.name,
                    passwordHash = user.password_hash,
                    email = user.email,
                    birthdate = user.date_of_birth.toString()
                )
            }
    }

    override suspend fun addUserAccount(
        username: String,
        email: String,
        birthdate: String,
        passwordHash: String
    ): Boolean {
        return arcadePhitoDB.usersQueries.insertUser(
            name = username,
            password_hash = passwordHash,
            email = email,
            date_of_birth = LocalDate.parse(birthdate)
        ).executeAsOneOrNull()?.let { it == username } ?: false
    }
}