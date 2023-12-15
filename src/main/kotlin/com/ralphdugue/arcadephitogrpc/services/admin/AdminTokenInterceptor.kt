package com.ralphdugue.arcadephitogrpc.services.admin

import com.ralphdugue.arcadephitogrpc.domain.admin.entities.VerifyAdminTokenParams
import com.ralphdugue.arcadephitogrpc.domain.admin.usecase.VerifyAdminToken
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.grpc.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class AdminTokenInterceptor(
    private val verifyAdminToken: VerifyAdminToken,
    private val logger: KLogger = KotlinLogging.logger {}
): ServerInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>?,
        headers: Metadata?,
        next: ServerCallHandler<ReqT, RespT>?
    ): ServerCall.Listener<ReqT> {
        val service = call?.methodDescriptor?.serviceName
        return when (service) {
            "admin.AdminService"-> {
                logger.info { "Received headers: $headers" }
                val token = headers?.get(Metadata.Key.of("Authorization" , Metadata.ASCII_STRING_MARSHALLER))
                    ?.substring(7)

                val verified = token?.let {
                    runBlocking(Dispatchers.IO) {
                        verifyAdminToken.execute(
                            VerifyAdminTokenParams(
                                token = it
                            )
                        )
                    }
                } ?: false

                if (verified) {
                    logger.info { "Admin token verified" }
                    next?.startCall(call, headers)!!
                } else {
                    logger.warn {  "Admin token not verified" }
                    call.close(
                        Status.UNAUTHENTICATED.withDescription("Invalid admin token"),
                        headers
                    )
                    object : ServerCall.Listener<ReqT>() {}
                }
            }
            else -> {
                logger.info { "Not an admin token use case, skipping verification" }
                next?.startCall(call, headers)!!
            }
        }
    }

}