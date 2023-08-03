package com.ralphdugue.arcadephito.backend.adapters.repositories

import com.ralphdugue.arcadephito.backend.adapters.database.APIUserTable
import com.ralphdugue.arcadephito.backend.adapters.database.APIUserTableRow
import com.ralphdugue.arcadephito.backend.di.DatabaseFactory.dbQuery
import com.ralphdugue.arcadephito.backend.domain.apiusers.repositories.ApiUsersRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class ApiUsersRepositoryImpl :ApiUsersRepository {

    override suspend fun getApiUser(entityId: String): APIUserTableRow? {
        return dbQuery {
            APIUserTable.select { APIUserTable.entityId eq entityId }
                .map {
                    APIUserTableRow(
                        entityId = it[APIUserTable.entityId],
                        apiKeyHash = it[APIUserTable.apiKeyHash],
                        apiSecretHash = it[APIUserTable.apiSecretHash]
                    )
                }
                .singleOrNull()
        }
    }

    override suspend fun addApiUser(entityId: String, keyHash: String, secretHash: String): APIUserTableRow {
        return dbQuery {
            APIUserTable.insert {
                it[this.entityId] = entityId
                it[this.apiKeyHash] = apiKeyHash
                it[this.apiSecretHash] = apiSecretHash
            }.insertedCount
        }.let {
            if (it == 0) throw Exception("User could not be added")
            APIUserTableRow(
                entityId = entityId,
                apiKeyHash = keyHash,
                apiSecretHash = secretHash
            )
        }
    }
}