package com.ralphdugue.arcadephitogrpc.domain.admin.entities

import com.ralphdugue.arcadephitogrpc.domain.Entity

data class AdminAccount(
    val devId: String,
    val email: String,
    val apiKeyHash: String,
    val apiSecretHash: String
): Entity

data class GenerateAdminTokenParams(
    val devId: String,
    val apiKey: String,
    val apiSecret: String
): Entity

data class ValidateAdminParams(
    val devId: String,
    val apiKey: String,
    val apiSecret: String,
): Entity

data class VerifyAdminTokenParams(val token: String): Entity

data class CreateAdminParams(
    val devId: String,
    val email: String
): Entity

data class CreateAdminResponse(
    val devId: String,
    val email: String,
    val apiKey: String,
    val apiSecret: String,
): Entity