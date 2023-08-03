package com.ralphdugue.arcadephito.backend.di

import com.ralphdugue.arcadephito.backend.adapters.graphql.mutations.CreateUserMutation
import com.ralphdugue.arcadephito.backend.adapters.graphql.queries.LoginUserQuery
import com.ralphdugue.arcadephito.backend.adapters.repositories.ApiUsersRepositoryImpl
import com.ralphdugue.arcadephito.backend.domain.appusers.repositories.AppUserRepository
import com.ralphdugue.arcadephito.backend.adapters.repositories.AppUserRepositoryImpl
import com.ralphdugue.arcadephito.backend.domain.apiusers.repositories.ApiUsersRepository
import com.ralphdugue.arcadephito.backend.domain.apiusers.usecase.ValidateAPIUser
import com.ralphdugue.arcadephito.backend.domain.appusers.usecase.LoginUser
import com.ralphdugue.arcadephito.backend.domain.appusers.usecase.RegisterUser
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val databaseModule = module {
    single<Database> { DatabaseFactory.create() }
}

val repositoryModule = module {
    single<AppUserRepository> { AppUserRepositoryImpl() }
    single<ApiUsersRepository> { ApiUsersRepositoryImpl() }
}

val useCaseModule = module {
    single { RegisterUser(get()) }
    single { LoginUser(get()) }
    single { ValidateAPIUser(get()) }
}

val schemaModule = module {
    single { CreateUserMutation(get()) }
    single { LoginUserQuery(get()) }
}