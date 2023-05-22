package com.ralphdugue.backend.arcadephito

import com.ralphdugue.backend.arcadephito.di.appModule
import com.ralphdugue.backend.arcadephito.modules.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>): Unit = EngineMain.main(args)


fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
    configureSerialization()
    configureSecurity()
    configureDatabases()
    configureHTTP()
}

