package com.ralphdugue.backend.arcadephito.routes

import com.ralphdugue.backend.arcadephito.data.repositories.UserRepository
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ralphdugue.backend.arcadephito.domain.LoginFields
import com.ralphdugue.backend.arcadephito.domain.RegistrationFields
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import org.koin.ktor.ext.inject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

fun Application.configureAuthRoutes() {

    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()

    val userService: UserRepository by inject()
    install(Sessions) {
        cookie<UserSession>("user_session", SessionStorageMemory()) {
            cookie.path = "/"

            cookie.extensions["SameSite"] = "lax"
            cookie.secure = true
            cookie.httpOnly = true
            val secretEncryptKey = hex("00112233445566778899aabbccddeeff")
            val secretSignKey = hex("6819b57a326945c1968f45236589")
            cookie.maxAge = Duration.parseOrNull("7d")
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }
    authentication {
        session<UserSession> {
            validate { session ->
                if (session.isExpired()) {
                    null
                } else {
                    session.copy(createdAt = System.currentTimeMillis())
                }
            }
        }
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
        post("/login"){
            val credentials = call.receive<LoginFields>()
            val user = userService.getUserFromLoginFields(credentials)
            user?.let {
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("username", credentials.username)
                    .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                    .sign(Algorithm.HMAC256(secret))
                call.sessions.set(UserSession(credentials.username))
                call.respond(HttpStatusCode.Accepted, hashMapOf("token" to token, "user" to user))
            } ?: call.respond(HttpStatusCode.NotFound)
        }
        post("/register") {
            val fields = call.receive<RegistrationFields>()
            val user = userService.createNewUser(fields.userTableRow())
            val token = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("username", fields.username)
                .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                .sign(Algorithm.HMAC256(secret))
            call.sessions.set(UserSession(fields.username))
            call.respond(
                HttpStatusCode.Created,
                hashMapOf("token" to token, "user" to user)
            )
        }
        get("/logout") {
            call.sessions.clear<UserSession>()
            call.respond(HttpStatusCode.OK)
        }
        get("/session") {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(hashMapOf("username" to session.sessionId))
            }
        }
//        authenticate("auth-oauth-google") {
//            get("login") {
//                call.respondRedirect("/callback")
//            }
//
//            get("/callback") {
//                val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()
//                call.sessions.set(UserSession(principal?.accessToken.toString()))
//                call.respondRedirect("/hello")
//            }
//        }
    }
}

data class UserSession(
    val sessionId: String,
    val oauthToken: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) : Principal {
    fun isExpired() = System.currentTimeMillis() - createdAt > TimeUnit.DAYS.toMillis(7)
}



