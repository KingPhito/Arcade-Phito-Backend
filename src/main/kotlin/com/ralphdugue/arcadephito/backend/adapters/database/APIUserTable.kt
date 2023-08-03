package com.ralphdugue.arcadephito.backend.adapters.database

import org.jetbrains.exposed.sql.Table

object APIUserTable : Table() {
    val entityId = varchar("entityId", length = 50).uniqueIndex()
    val apiKey = varchar("apiKey", length = 50).uniqueIndex()
    val apiSecret = varchar("apiSecret", length = 200)

    override val primaryKey = PrimaryKey(entityId)
}

data class APIUserTableRow(
    val entityId: String,
    val apiKey: String,
    val apiSecret: String
)