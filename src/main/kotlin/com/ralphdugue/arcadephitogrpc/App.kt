package com.ralphdugue.arcadephitogrpc

import com.ralphdugue.arcadephitogrpc.di.*
import com.ralphdugue.arcadephitogrpc.domain.config.ConfigRepository
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import com.ralphdugue.arcadephitogrpc.services.appuser.AppUserService
import com.ralphdugue.arcadephitogrpc.services.DeveloperService
import com.ralphdugue.arcadephitogrpc.services.appuser.UserServiceInterceptor
import io.github.oshai.kotlinlogging.KotlinLogging
import io.grpc.ServerBuilder
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.logger.slf4jLogger

class ArcadePhitoServer : KoinComponent {

    private val configRepository: ConfigRepository by inject()
    private val developerService: DeveloperService by inject()
    private val appUserService: AppUserService by inject()
    private val userServiceInterceptor: UserServiceInterceptor by inject()

    private val config: ArcadePhitoConfig by inject()

    private val logger = KotlinLogging.logger {}

    private val server = ServerBuilder
        .forPort(config.port)
        .addService(developerService).intercept(userServiceInterceptor)
        .addService(appUserService)
        .build()
    fun start() {
        GlobalScope.launch(Dispatchers.IO) {
            configRepository.initDatabase()
            withContext(Dispatchers.Main) {
                server.start()
                logger.info { "Server started, listening on ${config.port}" }
                Runtime.getRuntime().addShutdownHook(
                    Thread {
                        logger.info { "*** shutting down gRPC server since JVM is shutting down" }
                        this@ArcadePhitoServer.stop()
                        logger.info { "*** server shut down" }
                    }
                )
            }
        }
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }
}

fun main() {
    startKoin {
        slf4jLogger()
        modules(config, database, repositories, useCases, interceptors, services)
    }
    val server = ArcadePhitoServer()
    server.start()
    server.blockUntilShutdown()
}