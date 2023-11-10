package com.ralphdugue.arcadephitogrpc.domain.appusers.entities

import com.ralphdugue.arcadephitogrpc.domain.Entity

data class UserAccount(
    val username: String,
    val passwordHash: String,
    val email: String,
    val birthdate: String,
    val records: List<GameRecord> = listOf()
): Entity

data class GameRecord(
    val gameName: String,
    val score: String
): Entity