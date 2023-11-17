package com.ralphdugue.arcadephitogrpc.services.appuser

import com.google.rpc.Status
import com.google.type.Date
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.GenerateUserTokenParams
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.LoginAttemptParams
import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.RegisterUserParams
import com.ralphdugue.arcadephitogrpc.domain.appusers.usecases.GenerateUserToken
import com.ralphdugue.arcadephitogrpc.domain.appusers.usecases.RegisterAppUser
import com.ralphdugue.arcadephitogrpc.domain.appusers.usecases.VerifyLoginAttempt
import user.*
import user.Appuser.ArcadePhitoUser
import user.Appuser.CreateUserRequest
import java.time.LocalDate

class AppUserService(
    private val registerAppUser: RegisterAppUser,
    private val verifyLoginAttempt: VerifyLoginAttempt,
    private val generateUserToken: GenerateUserToken
) : AppUserServiceGrpcKt.AppUserServiceCoroutineImplBase() {

    override suspend fun createUser(request: CreateUserRequest): Appuser.CreateUserResponse {
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
            userCreated -> {
                val generatedToken = generateUserToken.execute(
                    GenerateUserTokenParams(
                        username = request.name,
                        password = request.password
                    )
                )
                if (!generatedToken.isNullOrBlank()) {
                    val user = arcadePhitoUser {
                        name = request.name
                        email = request.email
                        birthdate = request.birthdate
                    }
                    createUserResponse {
                        appUser = user
                        token = generatedToken
                        status = Status.newBuilder()
                            .setCode(200)
                            .setMessage("User created successfully")
                            .build()
                    }
                } else {
                    createUserResponse {
                        appUser = ArcadePhitoUser.getDefaultInstance()
                        status = Status.newBuilder()
                            .setCode(402)
                            .setMessage("User could not be created")
                            .build()
                    }
                }
            }
            else -> {
                createUserResponse {
                    appUser = ArcadePhitoUser.getDefaultInstance()
                    status = Status.newBuilder()
                        .setCode(402)
                        .setMessage("User could not be created")
                        .build()
                }
            }
        }
    }

    override suspend fun authenticateUser(request: Appuser.AuthenticateUserRequest): Appuser.AuthenticateUserResponse {
        val (userAuthenticated, user) = verifyLoginAttempt.execute(
            LoginAttemptParams(
                username = request.name,
                password = request.password
            )
        )
        return when {
            userAuthenticated -> {
                val localDate = LocalDate.parse(user!!.birthdate)
                val userAccount = arcadePhitoUser {
                    name = user.username
                    email = user.email
                    birthdate = Date.newBuilder()
                        .setYear(localDate.year)
                        .setMonth(localDate.monthValue)
                        .setDay(localDate.dayOfMonth)
                        .build()
                    records.apply { user.records.forEach { record -> put(record.gameName, record.score) }  }
                }
                authenticateUserResponse {
                    appUser = userAccount
                    status = Status.newBuilder()
                        .setCode(200)
                        .setMessage("User authenticated successfully")
                        .build()
                }
            }
            else -> {
                authenticateUserResponse {
                    appUser = ArcadePhitoUser.getDefaultInstance()
                    status = Status.newBuilder()
                        .setCode(401)
                        .setMessage("User could not be authenticated")
                        .build()
                }
            }
        }
    }
}