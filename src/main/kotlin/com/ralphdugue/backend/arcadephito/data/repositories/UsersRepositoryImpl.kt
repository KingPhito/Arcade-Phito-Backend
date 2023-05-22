package com.ralphdugue.backend.arcadephito.data.repositories

import com.ralphdugue.backend.arcadephito.data.db.UsersTable
import com.ralphdugue.backend.arcadephito.data.models.UserTableRow
import com.ralphdugue.backend.arcadephito.domain.LoginFields
import com.ralphdugue.backend.arcadephito.domain.UserResponse
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt


class UserRepositoryImpl(database: Database): UserRepository {

    init {
        transaction(database) {
            SchemaUtils.create(UsersTable)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun createNewUser(user: UserTableRow): UserResponse {
        return dbQuery {
            UsersTable.insert {
                it[username] = user.username
                it[email] = user.email
                it[passwordHash] = user.passwordHash
            }[UsersTable.id]
        }.let {
            UserResponse(
                username = user.username,
                email = user.email
            )
        }
    }

    override suspend fun getUserFromLoginFields(fields: LoginFields): UserResponse? {
        return dbQuery {
            UsersTable.select { UsersTable.username eq fields.username }
                .map {
                    UserTableRow(
                        username = it[UsersTable.username],
                        email = it[UsersTable.email],
                        passwordHash = it[UsersTable.passwordHash]
                    )
                }
                .map { row ->
                    if (BCrypt.checkpw(fields.password, row.passwordHash)) {
                        UserResponse(
                            username = row.username,
                            email = row.email
                        )
                    } else {
                        null
                    }
                }
                .singleOrNull()
        }
    }

    override suspend fun getUserByUsername(username: String): UserResponse? {
        return dbQuery {
            UsersTable.select { UsersTable.username eq username }
                .map {
                    UserTableRow(
                        username = it[UsersTable.username],
                        email = it[UsersTable.email],
                        passwordHash = it[UsersTable.passwordHash]
                    )
                }
                .map { row ->
                    UserResponse(
                        username = row.username,
                        email = row.email
                    )
                }
                .singleOrNull()
        }
    }

    override suspend fun updateUser(username: String, user: UserTableRow) {
        dbQuery {
            UsersTable.update({ UsersTable.username eq username }) {
                it[passwordHash] = user.passwordHash
                it[email] = user.email
            }
        }
    }

    override suspend fun deleteUser(username: String) {
        dbQuery {
            UsersTable.deleteWhere { UsersTable.username.eq(username) }
        }
    }
}