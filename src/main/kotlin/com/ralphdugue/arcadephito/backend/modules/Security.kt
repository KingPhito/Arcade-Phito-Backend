package com.ralphdugue.arcadephito.backend.modules

import io.ktor.server.auth.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureSecurity() {
    
    authentication {
        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile")
                )
            }
            client = HttpClient(Apache)
        }
    }

    val env = environment.config.propertyOrNull("ktor.environment")?.getString()
    val jwt = if (env == "dev") "jwt.dev" else "jwt.prod"
    val secret = environment.config.property("$jwt.secret").getString()
    val issuer = environment.config.property("$jwt.issuer").getString()
    val audience = environment.config.property("$jwt.audience").getString()
    val myRealm = environment.config.property("$jwt.realm").getString()

    authentication {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(JWT
                .require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build())

            challenge { _, _ ->
                call.respond(UnauthorizedResponse())
            }
        }
    }
    routing {
    }
}
