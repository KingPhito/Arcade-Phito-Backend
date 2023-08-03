package com.ralphdugue.arcadephito.backend.domain.appusers.repositories

import com.ralphdugue.arcadephito.backend.di.DatabaseFactory
import com.ralphdugue.arcadephito.backend.adapters.database.AppUserTableRow
import com.ralphdugue.arcadephito.backend.adapters.repositories.AppUserRepositoryImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.jetbrains.exposed.sql.Database
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mindrot.jbcrypt.BCrypt

@OptIn(ExperimentalCoroutinesApi::class)
class AppAppUserRepositoryTest {

    private lateinit var appUserRepository: AppUserRepository

    private lateinit var database: Database

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("IO thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        database = DatabaseFactory.create()
        appUserRepository = AppUserRepositoryImpl()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun `addUser should return the correct user information when user created successfully`() = runBlocking {
        val user = appUserRepository.addUser("phito", "phito@gmail.com", "password")
        val expectedUser = AppUserTableRow(
            username = "phito",
            email = "phito@gmail.com",
            passwordHash = "password"
        )
        assert(user.username == expectedUser.username)
        assert(user.email == expectedUser.email)
        assert(BCrypt.checkpw(expectedUser.passwordHash, user.passwordHash))
    }

    @Test
    fun `addUser should throw an exception when user already exists`(): Unit = runBlocking {
        appUserRepository.addUser("adrienne", "ahood@gmail.com", "password")
        assertThrows(Exception::class.java){
            runBlocking {
                appUserRepository.addUser("adrienne", "ahood@gmail.com", "password")
            }
        }
    }

    @Test
    fun `getUserByUsername should return the correct user information when user exists`() = runBlocking {
        appUserRepository.addUser("ralphdugue", "rdugue1@gmail.com", "password")
        val user = appUserRepository.getUserByUsername("ralphdugue")
        val expectedUser = AppUserTableRow(
            username = "ralphdugue",
            email = "rdugue1@gmail.com",
            passwordHash = "password"
        )
        assert(user!!.username == expectedUser.username)
        assert(user.email == expectedUser.email)
        assert(BCrypt.checkpw(expectedUser.passwordHash, user.passwordHash))
    }

    @Test
    fun `getUserByUsername should return null when user does not exist`() = runBlocking {
        val user = appUserRepository.getUserByUsername("Sky")
        assert(user == null)
    }
}