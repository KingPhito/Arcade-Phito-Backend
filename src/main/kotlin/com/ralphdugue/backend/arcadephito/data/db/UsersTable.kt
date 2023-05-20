package com.ralphdugue.backend.arcadephito.data.db

import org.jetbrains.exposed.sql.Table

object UsersTable : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", length = 50).uniqueIndex()
    val email = varchar("email", length = 50).uniqueIndex()
    val passwordHash = varchar("email", length = 200)

    override val primaryKey = PrimaryKey(id)
}