package com.ralphdugue.arcadephito.backend.di

import com.ralphdugue.arcadephito.backend.adapters.graphql.mutations.CreateUserMutation
import com.ralphdugue.arcadephito.backend.adapters.graphql.queries.LoginUserQuery
import com.ralphdugue.arcadephito.backend.domain.repositories.UserRepository
import com.ralphdugue.arcadephito.backend.adapters.repositories.UserRepositoryImpl
import com.ralphdugue.arcadephito.backend.domain.usecase.LoginUser
import com.ralphdugue.arcadephito.backend.domain.usecase.RegisterUser
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val databaseModule = module {
    single<Database> { DatabaseFactory.create() }
}

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl() }
}

val useCaseModule = module {
    single { RegisterUser(get()) }
    single { LoginUser(get()) }
}

val schemaModule = module {
    single { CreateUserMutation(get()) }
    single { LoginUserQuery(get()) }
}