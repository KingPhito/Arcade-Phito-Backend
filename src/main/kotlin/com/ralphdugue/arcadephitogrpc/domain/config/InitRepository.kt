package com.ralphdugue.arcadephitogrpc.domain.config

interface InitRepository {

    suspend fun initDatabase()
}