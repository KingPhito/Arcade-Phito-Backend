package com.ralphdugue.arcadephito.backend.adapters.database

import org.jetbrains.exposed.sql.Table

object UsersTable : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", length = 50).uniqueIndex()
    val email = varchar("email", length = 50).uniqueIndex()
    val passwordHash = varchar("password", length = 200)

    override val primaryKey = PrimaryKey(id)
}

data class UserTableRow(
    val username: String,
    val email: String,
    val passwordHash: String
)