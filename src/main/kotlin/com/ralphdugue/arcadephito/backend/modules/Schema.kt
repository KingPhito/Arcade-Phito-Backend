package com.ralphdugue.arcadephito.backend.modules

import com.apurebase.kgraphql.GraphQL
import com.ralphdugue.arcadephito.backend.domain.entities.LoginFields
import com.ralphdugue.arcadephito.backend.domain.entities.RegistrationFields
import com.ralphdugue.arcadephito.backend.domain.entities.User
import com.ralphdugue.arcadephito.backend.domain.repositories.UserRepository
import com.ralphdugue.arcadephito.backend.domain.usecase.LoginUser
import com.ralphdugue.arcadephito.backend.domain.usecase.RegisterUser
import io.ktor.server.application.*
import io.ktor.server.auth.*
import org.koin.ktor.ext.inject

fun Application.configureSchema() {
    val userRepository: UserRepository by inject()
    install(GraphQL) {
        playground = true

//        wrap {
//            authenticate(optional = true, build = it)
//        }
//
//        context { call ->
//            call.authentication.principal<User>()?.let {
//                +it
//            }
//        }

        schema {
            configure {
                useDefaultPrettyPrinter = true
            }

            mutation("RegisterUser") {
                resolver { username: String, email: String, password: String ->
                    try {
                        val fields = RegistrationFields(username, email, password)
                        RegisterUser(userRepository).execute(fields)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }

            query("LoginUser") {
                resolver { username: String, password: String ->
                    try {
                        val fields = LoginFields(username, password)
                        LoginUser(userRepository).execute(fields)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }

            type<User>()
        }
    }
}