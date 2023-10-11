package com.ralphdugue.arcadephitogrpc.domain.config

interface ConfigRepository {

    suspend fun initDatabase()
}