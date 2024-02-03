package com.ralphdugue.arcadephitogrpc.services.appuser

import com.ralphdugue.arcadephitogrpc.domain.appusers.entities.VerifyUserTokenParams
import com.ralphdugue.arcadephitogrpc.domain.appusers.usecases.VerifyUserToken
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.grpc.*
import kotlinx.coroutines.runBlocking

class UserTokenInterceptor(
    private val verifyUserToken: VerifyUserToken,
    private val logger: KLogger = KotlinLogging.logger {},
) : ServerInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>?,
        headers: Metadata?,
        next: ServerCallHandler<ReqT, RespT>?,
    ): ServerCall.Listener<ReqT> {
        val service = call?.methodDescriptor?.serviceName
        return when (service) {
            "admin.AdminService",
            "developer.DeveloperService",
            "appuser.AppUserService",
            -> {
                logger.info { "Not a user token use case, skipping verification" }
                next?.startCall(call, headers)!!
            }
            else -> {
                logger.info { "Received headers: $headers" }
                val token =
                    headers?.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER))
                        ?.substring(7)

                val verified =
                    token?.let {
                        runBlocking {
                            verifyUserToken.execute(
                                VerifyUserTokenParams(
                                    token = it,
                                ),
                            )
                        }
                    } ?: false

                if (verified) {
                    logger.info { "User token verified" }
                    next?.startCall(call, headers)!!
                } else {
                    logger.warn { "User token not verified" }
                    call?.close(
                        Status.UNAUTHENTICATED.withDescription("Invalid user token"),
                        headers,
                    )
                    object : ServerCall.Listener<ReqT>() {}
                }
            }
        }
    }
}
