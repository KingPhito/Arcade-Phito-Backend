package com.ralphdugue.arcadephito.backend.domain.appusers.usecase

import com.ralphdugue.arcadephito.backend.adapters.database.AppUserTableRow
import com.ralphdugue.arcadephito.backend.domain.appusers.entities.LoginFields
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
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mindrot.jbcrypt.BCrypt

class LoginUserTest {

    @get:Rule
    val mockKRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var appUserRepository: AppUserRepository

    private lateinit var loginUser: LoginUser

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("IO thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        loginUser = LoginUser(appUserRepository)
    }

    @Test
    fun `LoginUser should return the correct user information when user created successfully`() = runBlocking {
        val fields = LoginFields("phito", "password")
        coEvery { appUserRepository.getUserByUsername(any()) } returns AppUserTableRow(
            username = fields.username,
            email = "fake@mail.com",
            passwordHash = BCrypt.hashpw(fields.password, BCrypt.gensalt())
        )
        val user = loginUser.execute(fields)
        assert(user.username == fields.username)
        assert(user.email == "fake@mail.com")
    }

    @Test
    fun `LoginUser should throw an exception when user not found`(): Unit = runBlocking {
        val fields = LoginFields("phito", "password")
        coEvery { appUserRepository.getUserByUsername(any()) } returns null
        assertThrows(Exception::class.java) {
            runBlocking {
                loginUser.execute(fields)
            }
        }
    }

    @Test
    fun `LoginUser should throw an exception when password is incorrect`(): Unit = runBlocking {
        val fields = LoginFields("phito", "password")
        coEvery { appUserRepository.getUserByUsername(any()) } returns AppUserTableRow(
            username = fields.username,
            email = "fake@gmail.com",
            passwordHash = BCrypt.hashpw("wrongpassword", BCrypt.gensalt())
        )
        assertThrows(Exception::class.java) {
            runBlocking {
                loginUser.execute(fields)
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }
}