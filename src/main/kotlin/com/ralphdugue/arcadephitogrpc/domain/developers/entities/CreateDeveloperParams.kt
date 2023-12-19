package com.ralphdugue.arcadephitogrpc.domain.developers.entities

import com.ralphdugue.arcadephitogrpc.domain.Entity

data class CreateDeveloperParams(
    val devId: String,
    val email: String,
): Entity

data class CreateDeveloperResponse(
    val devId: String,
    val email: String,
    val apiKey: String,
    val apiSecret: String,
): Entity

data class DeveloperAccount(
    val devId: String,
    val email: String,
    val apiKeyHash: String,
    val apiSecretHash: String
): Entity

data class GenerateDeveloperTokenParams(
    val devId: String,
    val apiKey: String,
    val apiSecret: String
): Entity

data class ValidateDeveloperParams(
    val devId: String,
    val apiKey: String,
    val apiSecret: String,
): Entity

data class VerifyDeveloperTokenParams(val token: String): Entity