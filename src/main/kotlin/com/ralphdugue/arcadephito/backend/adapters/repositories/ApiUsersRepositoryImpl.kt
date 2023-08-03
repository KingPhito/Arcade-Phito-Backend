package com.ralphdugue.arcadephito.backend.adapters.repositories

import com.ralphdugue.arcadephito.backend.adapters.database.APIUserTable
import com.ralphdugue.arcadephito.backend.adapters.database.APIUserTableRow
import com.ralphdugue.arcadephito.backend.di.DatabaseFactory.dbQuery
import com.ralphdugue.arcadephito.backend.domain.apiusers.repositories.ApiUsersRepository
import org.jetbrains.exposed.sql.select

class ApiUsersRepositoryImpl :ApiUsersRepository {

    override suspend fun getApiUser(entityId: String): APIUserTableRow? {
        return dbQuery {
            APIUserTable.select { APIUserTable.entityId eq entityId }
                .map {
                    APIUserTableRow(
                        entityId = it[APIUserTable.entityId],
                        apiKey = it[APIUserTable.apiKey],
                        apiSecret = it[APIUserTable.apiSecret]
                    )
                }
                .singleOrNull()
        }
    }
}