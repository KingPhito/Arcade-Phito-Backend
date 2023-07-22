package com.ralphdugue.arcadephito.backend.di

import com.ralphdugue.arcadephito.backend.data.database.DatabaseFactory
import com.ralphdugue.arcadephito.backend.domain.repositories.UserRepository
import com.ralphdugue.arcadephito.backend.data.repositories.UserRepositoryImpl
import com.ralphdugue.arcadephito.backend.domain.usecase.LoginUser
import com.ralphdugue.arcadephito.backend.domain.usecase.RegisterUser
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val databaseModule = module {
    single<Database> { DatabaseFactory.create() }
}

val userModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<RegisterUser> { RegisterUser(get()) }
    single<LoginUser> { LoginUser(get()) }
}