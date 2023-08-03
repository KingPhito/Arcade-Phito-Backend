package com.ralphdugue.arcadephito.backend.domain.appusers.usecase

import com.ralphdugue.arcadephito.backend.adapters.database.AppUserTableRow
import com.ralphdugue.arcadephito.backend.domain.appusers.entities.RegistrationFields
import com.ralphdugue.arcadephito.backend.domain.appusers.repositories.AppUserRepository
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterUserTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var appUserRepository: AppUserRepository

    private lateinit var registerUser: RegisterUser

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("IO thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        registerUser = RegisterUser(appUserRepository)
    }

    @Test
    fun `RegisterUser should return the correct user information when user created successfully`() = runBlocking {
        val fields = RegistrationFields("r", "phito", "p")
        coEvery { appUserRepository.addUser(any(), any(), any()) } returns AppUserTableRow(
            username = fields.username,
            email = fields.email,
            passwordHash = fields.password
        )
        val user = registerUser.execute(fields)
        assert(user.username == fields.username)
        assert(user.email == fields.email)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

}