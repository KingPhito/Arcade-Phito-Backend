package com.ralphdugue.arcadephitogrpc.services.developer

import com.ralphdugue.arcadephitogrpc.domain.developers.entities.VerifyDeveloperTokenParams
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.VerifyDeveloperToken
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status
import kotlinx.coroutines.runBlocking

class DevTokenInterceptor(
    private val verifyDeveloperToken: VerifyDeveloperToken,
    private val logger: KLogger = KotlinLogging.logger {},
) : ServerInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>?,
        headers: Metadata?,
        next: ServerCallHandler<ReqT, RespT>?,
    ): ServerCall.Listener<ReqT> {
        val service = call?.methodDescriptor?.serviceName
        return when (service) {
            "user.appuser.AppUserService" -> {
                logger.info { "Received headers: $headers" }
                val token =
                    headers?.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER))
                        ?.substring(7)

                val verified =
                    token?.let {
                        runBlocking {
                            verifyDeveloperToken.execute(
                                VerifyDeveloperTokenParams(
                                    token = it,
                                ),
                            )
                        }
                    } ?: false

                if (verified) {
                    logger.info { "Developer token verified" }
                    next?.startCall(call, headers)!!
                } else {
                    logger.warn { "Developer token not verified" }
                    call.close(
                        Status.UNAUTHENTICATED.withDescription("Invalid developer token"),
                        headers,
                    )
                    object : ServerCall.Listener<ReqT>() {}
                }
            }
            else -> {
                logger.info { "Not a dev token use case, skipping verification" }
                next?.startCall(call, headers)!!
            }
        }
    }
}
