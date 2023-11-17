package com.ralphdugue.arcadephitogrpc.domain.appusers.entities

import com.ralphdugue.arcadephitogrpc.domain.Entity
import java.time.LocalDate

data class RegisterUserParams(
    val username: String,
    val email: String,
    val birthdate: LocalDate,
    val password: String,
): Entity

data class RetrieveAppUserParams(val username: String): Entity

data class LoginAttemptParams(val username: String, val password: String): Entity

data class VerifyUserTokenParams(val token: String): Entity

data class GenerateUserTokenParams(val username: String, val password: String): Entity

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