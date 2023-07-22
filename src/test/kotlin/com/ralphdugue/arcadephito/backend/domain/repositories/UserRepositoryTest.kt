package com.ralphdugue.arcadephito.backend.domain.repositories

import com.ralphdugue.arcadephito.backend.data.database.DatabaseFactory
import com.ralphdugue.arcadephito.backend.data.database.UserTableRow
import com.ralphdugue.arcadephito.backend.data.repositories.UserRepositoryImpl
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
class UserRepositoryTest {

    private lateinit var userRepository: UserRepository

    private lateinit var database: Database

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("IO thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        database = DatabaseFactory.create()
        userRepository = UserRepositoryImpl()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun `addUser should return the correct user information when user created successfully`() = runBlocking {
        val user = userRepository.addUser("phito", "phito@gmail.com", "password")
        val expectedUser = UserTableRow(
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
        userRepository.addUser("adrienne", "ahood@gmail.com", "password")
        assertThrows(Exception::class.java){
            runBlocking {
                userRepository.addUser("adrienne", "ahood@gmail.com", "password")
            }
        }
    }

    @Test
    fun `getUserByUsername should return the correct user information when user exists`() = runBlocking {
        userRepository.addUser("ralphdugue", "rdugue1@gmail.com", "password")
        val user = userRepository.getUserByUsername("ralphdugue")
        val expectedUser = UserTableRow(
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
        val user = userRepository.getUserByUsername("Sky")
        assert(user == null)
    }
}