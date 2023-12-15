package com.ralphdugue.arcadephitogrpc.services.admin

import admin.Admin
import admin.AdminServiceGrpcKt
import admin.createDeveloperResponse
import com.google.rpc.Status
import com.ralphdugue.arcadephitogrpc.domain.developers.entities.DeveloperEntities
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.CreateDeveloper

class AdminService(
    private val createDeveloper: CreateDeveloper,
) : AdminServiceGrpcKt.AdminServiceCoroutineImplBase() {

    override suspend fun createDeveloper(request: Admin.CreateDeveloperRequest): Admin.CreateDeveloperResponse {
        val params = DeveloperEntities(
            devId = request.developerId,
            email = request.email,
        )
        val (success, response) = createDeveloper.execute(params)
        return if (success) {
            createDeveloperResponse {
                apiKey = response?.apiKey ?: ""
                apiSecret = response?.apiSecret ?: ""
                developerId = response?.devId ?: ""
                status = Status.newBuilder()
                    .setCode(200)
                    .setMessage("Developer created successfully")
                    .build()
            }
        } else {
            createDeveloperResponse {
                apiKey = ""
                apiSecret = ""
                developerId = ""
                status = Status.newBuilder()
                    .setCode(402)
                    .setMessage("Developer could not be created")
                    .build()

            }
        }
    }
}