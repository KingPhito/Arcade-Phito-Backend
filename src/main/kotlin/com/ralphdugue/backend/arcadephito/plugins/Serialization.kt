package com.ralphdugue.backend.arcadephito.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ServerContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    routing {
        get("/json/kotlinx-serialization") {
                call.respond(HelloWorld())
            }
    }
}

@Serializable
data class HelloWorld(val hello: String = "hello", val world: String = "world")