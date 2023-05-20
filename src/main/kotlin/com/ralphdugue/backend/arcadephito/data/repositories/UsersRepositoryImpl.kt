package com.ralphdugue.backend.arcadephito.data.repositories

import com.ralphdugue.backend.arcadephito.data.db.UsersTable
import com.ralphdugue.backend.arcadephito.data.models.User
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*


class UserRepositoryImpl(database: Database): UserRepository {

    init {
        transaction(database) {
            SchemaUtils.create(UsersTable)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun create(user: User): Int = dbQuery {
        UsersTable.insert {
            it[username] = user.username
            it[email] = user.email
            it[passwordHash] = user.passwordHash
        }[UsersTable.id]
    }

    override suspend fun read(username: String): User? {
        return dbQuery {
            UsersTable.select { UsersTable.username eq username }
                .map {
                    User(
                        username = it[UsersTable.username],
                        email = it[UsersTable.email],
                        passwordHash = it[UsersTable.passwordHash]
                    )
                }
                .singleOrNull()
        }
    }

    override suspend fun update(username: String, user: User) {
        dbQuery {
            UsersTable.update({ UsersTable.username eq username }) {
                it[passwordHash] = user.passwordHash
                it[email] = user.email
            }
        }
    }

    override suspend fun delete(username: String) {
        dbQuery {
            UsersTable.deleteWhere { UsersTable.username.eq(username) }
        }
    }
}