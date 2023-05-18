package com.arcadephito.ralphdugue.backend.arcadephito.plugins

import com.arcadephito.ralphdugue.backend.arcadephito.controllers.UserController
import com.arcadephito.ralphdugue.backend.arcadephito.data.models.User
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Application.configureDatabases() {

    val userService: UserController by inject()
    install(Resources)
    routing {
        // Create user
        authenticate("auth-oauth-google", "auth-jwt") {
            post<Users> {
                val user = call.receive<User>()
                val id = userService.create(user)
                call.respond(HttpStatusCode.Created, id)
            }
            // Read user
            get<Users.Username> { users ->
                val username = users.username
                val user = userService.read(username)
                if (user != null) {
                    call.respond(HttpStatusCode.OK, user.copy(passwordHash = ""))
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            // Update user
            put<Users.Username> { users ->
                val username = users.username
                val user = call.receive<User>()
                userService.update(username, user)
                call.respond(HttpStatusCode.OK)
            }
            // Delete user
            delete<Users.Username> { users ->
                val username = users.username
                userService.delete(username)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

@Serializable
@Resource("/users")
class Users {

    @Serializable
    @Resource("{username}")
    class Username(val parent: Users = Users(), val username: String)
}

