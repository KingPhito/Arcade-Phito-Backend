package com.ralphdugue.arcadephito.backend.modules

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ralphdugue.arcadephito.backend.domain.apiusers.entities.JWTRequest
import com.ralphdugue.arcadephito.backend.domain.apiusers.usecase.ValidateAPIUser
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {

    val env = environment.config.propertyOrNull("ktor.environment")?.getString()
    val jwt = if (env == "dev") "jwt.dev" else "jwt.prod"
    val secret = environment.config.property("$jwt.secret").getString()
    val issuer = environment.config.property("$jwt.issuer").getString()
    val audience = environment.config.property("$jwt.audience").getString()
    val myRealm = environment.config.property("$jwt.realm").getString()

    val validateAPIUser: ValidateAPIUser by inject()

    authentication {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(JWT
                .require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build())

            validate { credential ->
                if (credential.payload.getClaim("entityId").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(UnauthorizedResponse())
            }
        }

//        oauth("auth-oauth-google") {
//            urlProvider = { "http://localhost:8080/callback" }
//            providerLookup = {
//                OAuthServerSettings.OAuth2ServerSettings(
//                    name = "google",
//                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
//                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
//                    requestMethod = HttpMethod.Post,
//                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
//                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
//                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile")
//                )
//            }
//            client = HttpClient(Apache)
//        }
    }
    routing {
        install(ContentNegotiation) {
            json()
        }
        post("/auth") {
            val request = call.receive<JWTRequest>()

            if (!validateAPIUser.execute(request)) {
                call.respond(UnauthorizedResponse())
                return@post
            }

            val token = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("entityId", request.entityId)
                .sign(Algorithm.HMAC256(secret))

            call.respond(mapOf("token" to token))
        }
    }
}
