package com.ralphdugue.arcadephito.backend

import com.ralphdugue.arcadephito.backend.di.databaseModule
import com.ralphdugue.arcadephito.backend.di.userModule
import com.ralphdugue.arcadephito.backend.modules.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    install(ContentNegotiation) {
        json()
    }
    install(Koin) {
        slf4jLogger()
        modules(databaseModule, userModule)
    }
    //configureSecurity()
    configureSchema()
    configureHTTP()
}
