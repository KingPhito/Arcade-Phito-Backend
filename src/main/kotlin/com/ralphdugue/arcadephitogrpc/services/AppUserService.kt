package com.ralphdugue.arcadephitogrpc.services

import com.google.rpc.Status
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.LoginAttemptParams
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.RegisterUserParams
import com.ralphdugue.arcadephitogrpc.domain.appusers.usecases.RegisterAppUser
import com.ralphdugue.arcadephitogrpc.domain.appusers.usecases.VerifyLoginAttempt
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.VerifyDeveloperTokenParams
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.VerifyDeveloperToken
import user.AppUserServiceGrpcKt
import user.Appuser
import user.Appuser.ArcadePhitoUser
import user.Appuser.CreateUserRequest
import java.time.LocalDate

class AppUserService(
    private val registerAppUser: RegisterAppUser,
    private val verifyLoginAttempt: VerifyLoginAttempt,
    private val verifyDeveloperToken: VerifyDeveloperToken
) : AppUserServiceGrpcKt.AppUserServiceCoroutineImplBase() {

    override suspend fun createUser(request: CreateUserRequest): Appuser.CreateUserResponse {
        val validToken = verifyDeveloperToken.execute(VerifyDeveloperTokenParams(request.token))
        val userCreated = registerAppUser.execute(
            RegisterUserParams(
                username = request.name,
                email = request.email,
                birthdate = LocalDate.of(
                    request.birthdate.year,
                    request.birthdate.month,
                    request.birthdate.day
                ),
                password = request.password
            )
        )
        return when {
            !validToken -> {
                val status = Status.newBuilder()
                    .setCode(401)
                    .setMessage("Invalid token")
                    .build()
                Appuser.CreateUserResponse.newBuilder()
                    .setStatus(status)
                    .build()
            }
            userCreated -> {
                val user = ArcadePhitoUser.newBuilder()
                    .setName(request.name)
                    .setEmail(request.email)
                    .setBirthdate(request.birthdate)
                    .build()
                val status = Status.newBuilder()
                    .setCode(200)
                    .setMessage("User created successfully")
                    .build()
                Appuser.CreateUserResponse.newBuilder()
                    .setUser(user)
                    .setStatus(status)
                    .build()
            }
            else -> {
                val status = Status.newBuilder()
                    .setCode(500)
                    .setMessage("User could not be created")
                    .build()
                Appuser.CreateUserResponse.newBuilder()
                    .setStatus(status)
                    .build()
            }
        }
    }

    override suspend fun authenticateUser(request: Appuser.AuthenticateUserRequest): Appuser.AuthenticateUserResponse {
        val userAuthenticated = verifyLoginAttempt.execute(
            LoginAttemptParams(
                username = request.name,
                password = request.password
            )
        )
        val validToken = verifyDeveloperToken.execute(VerifyDeveloperTokenParams(request.token))
        return when {
            !validToken -> {
                val status = Status.newBuilder()
                    .setCode(401)
                    .setMessage("Invalid token")
                    .build()
                Appuser.AuthenticateUserResponse.newBuilder()
                    .setStatus(status)
                    .build()
            }
            userAuthenticated -> {
                val status = Status.newBuilder()
                    .setCode(200)
                    .setMessage("User authenticated successfully")
                    .build()
                Appuser.AuthenticateUserResponse.newBuilder()
                    .setStatus(status)
                    .build()
            }
            else -> {
                val status = Status.newBuilder()
                    .setCode(402)
                    .setMessage("Invalid credentials")
                    .build()
                Appuser.AuthenticateUserResponse.newBuilder()
                    .setStatus(status)
                    .build()
            }
        }
    }
}