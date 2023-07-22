package com.ralphdugue.arcadephito.backend.data.repositories

import com.ralphdugue.arcadephito.backend.data.database.DatabaseFactory.dbQuery
import com.ralphdugue.arcadephito.backend.data.database.UserTableRow
import com.ralphdugue.arcadephito.backend.data.database.UsersTable
import com.ralphdugue.arcadephito.backend.domain.repositories.UserRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.mindrot.jbcrypt.BCrypt

class UserRepositoryImpl : UserRepository {

        override suspend fun addUser(username: String, email: String, password: String): UserTableRow {
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
        return dbQuery {
            UsersTable.insert {
                it[this.username] = username
                it[this.email] = email
                it[this.passwordHash] = passwordHash
            }.insertedCount
        }.let {
            if (it == 0) throw Exception("User already exists")
            UserTableRow(
                username = username,
                email = email,
                passwordHash = passwordHash
            )
        }
    }

    override suspend fun getUserByUsername(username: String): UserTableRow? {
        return dbQuery {
            UsersTable.select { UsersTable.username eq username }
                .map {
                    UserTableRow(
                        username = it[UsersTable.username],
                        email = it[UsersTable.email],
                        passwordHash = it[UsersTable.passwordHash]
                    )
                }
                .singleOrNull()
        }
    }

}