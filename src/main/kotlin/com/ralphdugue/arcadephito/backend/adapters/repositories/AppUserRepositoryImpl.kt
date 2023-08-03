package com.ralphdugue.arcadephito.backend.adapters.repositories

import com.ralphdugue.arcadephito.backend.di.DatabaseFactory.dbQuery
import com.ralphdugue.arcadephito.backend.adapters.database.AppUserTableRow
import com.ralphdugue.arcadephito.backend.adapters.database.AppUsersTable
import com.ralphdugue.arcadephito.backend.domain.appusers.repositories.AppUserRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.mindrot.jbcrypt.BCrypt

class AppUserRepositoryImpl : AppUserRepository {

    override suspend fun addUser(username: String, email: String, password: String): AppUserTableRow {
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
        return dbQuery {
            AppUsersTable.insert {
                it[this.username] = username
                it[this.email] = email
                it[this.passwordHash] = passwordHash
            }.insertedCount
        }.let {
            if (it == 0) throw Exception("User already exists")
            AppUserTableRow(
                username = username,
                email = email,
                passwordHash = passwordHash
            )
        }
    }

    override suspend fun getUserByUsername(username: String): AppUserTableRow? {
        return dbQuery {
            AppUsersTable.select { AppUsersTable.username eq username }
                .map {
                    AppUserTableRow(
                        username = it[AppUsersTable.username],
                        email = it[AppUsersTable.email],
                        passwordHash = it[AppUsersTable.passwordHash]
                    )
                }
                .singleOrNull()
        }
    }

}