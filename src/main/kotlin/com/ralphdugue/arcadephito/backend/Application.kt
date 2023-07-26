package com.ralphdugue.arcadephito.backend

import com.ralphdugue.arcadephito.backend.di.databaseModule
import com.ralphdugue.arcadephito.backend.di.repositoryModule
import com.ralphdugue.arcadephito.backend.di.schemaModule
import com.ralphdugue.arcadephito.backend.di.useCaseModule
import com.ralphdugue.arcadephito.backend.modules.configureSchema
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
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
    install(Koin) {
        slf4jLogger()
        modules(databaseModule, repositoryModule, useCaseModule, schemaModule)
    }
    //configureSecurity()
    configureSchema()
}
