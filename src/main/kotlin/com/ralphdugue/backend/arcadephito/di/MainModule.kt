package com.ralphdugue.backend.arcadephito.di

import com.ralphdugue.backend.arcadephito.data.repositories.UserRepository
import com.ralphdugue.backend.arcadephito.data.repositories.UserRepositoryImpl
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module


val appModule = module {
    single {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "root",
            driver = "org.h2.Driver",
            password = ""
        )
    }
    single<UserRepository> { UserRepositoryImpl(get()) }
}