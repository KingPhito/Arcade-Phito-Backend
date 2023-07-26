package com.ralphdugue.arcadephito.backend.domain.usecase

import com.ralphdugue.arcadephito.backend.adapters.database.UserTableRow
import com.ralphdugue.arcadephito.backend.domain.entities.RegistrationFields
import com.ralphdugue.arcadephito.backend.domain.repositories.UserRepository
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
    private lateinit var userRepository: UserRepository

    private lateinit var registerUser: RegisterUser

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("IO thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        registerUser = RegisterUser(userRepository)
    }

    @Test
    fun `RegisterUser should return the correct user information when user created successfully`() = runBlocking {
        val fields = RegistrationFields("r", "phito", "p")
        coEvery { userRepository.addUser(any(), any(), any()) } returns UserTableRow(
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