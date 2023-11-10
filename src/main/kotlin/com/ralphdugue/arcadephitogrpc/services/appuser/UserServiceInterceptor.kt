package com.ralphdugue.arcadephitogrpc.services.appuser

import io.grpc.Status
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.VerifyDeveloperTokenParams
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.VerifyDeveloperToken
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class UserServiceInterceptor(
    private val verifyDeveloperToken: VerifyDeveloperToken,
    private val logger: KLogger = KotlinLogging.logger {}
): ServerInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>?,
        headers: Metadata?,
        next: ServerCallHandler<ReqT, RespT>?
    ): ServerCall.Listener<ReqT> {
        logger.info { "Received headers: $headers" }
        val token = headers?.get(Metadata.Key.of("Authorization" , Metadata.ASCII_STRING_MARSHALLER))
            ?.substring(7)

        val verified = token?.let {
            runBlocking(Dispatchers.IO) {
                verifyDeveloperToken.execute(
                    VerifyDeveloperTokenParams(
                        token = it
                    )
                )
            }
        } ?: false

        return if (verified) {
            logger.info { "Developer token verified" }
            next?.startCall(call, headers)!!
        } else {
            logger.info { "Developer token not verified" }
            call?.close(
                Status.UNAUTHENTICATED.withDescription("Invalid developer token"),
                headers
            )
            next?.startCall(call, headers)!!
        }
    }
}