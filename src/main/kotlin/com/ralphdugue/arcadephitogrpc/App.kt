package com.ralphdugue.arcadephitogrpc

import com.ralphdugue.arcadephitogrpc.di.*
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import com.ralphdugue.arcadephitogrpc.services.AppUserService
import com.ralphdugue.arcadephitogrpc.services.DeveloperService
import io.grpc.ServerBuilder
import io.grpc.ServerServiceDefinition
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.logger.slf4jLogger

class ArcadePhitoServer : KoinComponent {

    private val config: ArcadePhitoConfig by inject()
    private val developerService: DeveloperService by inject()
    private val appUserService: AppUserService by inject()

    private val server = ServerBuilder
        .forPort(config.port)
        .addService(developerService)
        .addService(appUserService)
        .build()
    fun start() {
        server.start()
        println("Server started, listening on ${config.port}")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                this@ArcadePhitoServer.stop()
                println("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }
}

fun main(args: Array<String>) {
    startKoin {
        slf4jLogger()
        modules(config, database, repositories, useCases, services)
    }
    val server = ArcadePhitoServer()
    server.start()
    server.blockUntilShutdown()
}