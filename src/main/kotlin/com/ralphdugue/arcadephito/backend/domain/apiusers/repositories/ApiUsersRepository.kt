package com.ralphdugue.arcadephito.backend.domain.apiusers.repositories

import com.ralphdugue.arcadephito.backend.adapters.database.APIUserTableRow

interface ApiUsersRepository {

    suspend fun getApiUser(entityId: String): APIUserTableRow?

    suspend fun addApiUser(
        entityId: String,
        keyHash: String,
        secretHash: String,
    ): APIUserTableRow
}