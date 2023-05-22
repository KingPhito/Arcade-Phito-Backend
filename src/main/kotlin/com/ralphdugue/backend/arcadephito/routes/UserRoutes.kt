package com.ralphdugue.backend.arcadephito.routes

import com.ralphdugue.backend.arcadephito.data.repositories.UserRepository
import com.ralphdugue.backend.arcadephito.data.models.UserTableRow
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

fun Application.configureUserRoutes() {

    val userService: UserRepository by inject()
    install(Resources)
    routing {
        // Create user
        authenticate("auth-oauth-google", "auth-jwt") {
            post<Users> {
                val user = call.receive<UserTableRow>()
                val id = userService.createNewUser(user)
                call.respond(HttpStatusCode.Created, id)
            }
            // Read user
            get<Users.Username> { users ->
                val username = users.username
                val user = userService.getUserByUsername(username)
                if (user != null) {
                    call.respond(HttpStatusCode.OK, )
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            // Update user
            put<Users.Username> { users ->
                val username = users.username
                val user = call.receive<UserTableRow>()
                userService.updateUser(username, user)
                call.respond(HttpStatusCode.OK)
            }
            // Delete user
            delete<Users.Username> { users ->
                val username = users.username
                userService.deleteUser(username)
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

