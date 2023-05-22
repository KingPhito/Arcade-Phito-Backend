package com.ralphdugue.backend.arcadephito

import com.ralphdugue.backend.arcadephito.plugins.HelloWorld
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals


class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.get("/json/kotlinx-serialization").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(HelloWorld(), body())
        }
    }
}
