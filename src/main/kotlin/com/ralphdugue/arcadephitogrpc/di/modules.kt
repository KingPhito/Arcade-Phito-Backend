package com.ralphdugue.arcadephitogrpc.di

import app.cash.sqldelight.db.SqlDriver
import com.ralphdugue.arcadephitogrpc.ArcadePhito
import com.ralphdugue.arcadephitogrpc.adapters.DatabaseFactory
import com.ralphdugue.arcadephitogrpc.adapters.appusers.AppUserRepositoryImpl
import com.ralphdugue.arcadephitogrpc.adapters.config.ArcadePhitoConfigFactory
import com.ralphdugue.arcadephitogrpc.adapters.developers.DeveloperRepositoryImpl
import com.ralphdugue.arcadephitogrpc.domain.appusers.AppUserRepository
import com.ralphdugue.arcadephitogrpc.domain.appusers.usecases.RegisterAppUser
import com.ralphdugue.arcadephitogrpc.domain.appusers.usecases.VerifyLoginAttempt
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.CreateDeveloper
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.GenerateDeveloperToken
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.ValidateDeveloper
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.VerifyDeveloperToken
import com.ralphdugue.arcadephitogrpc.services.AppUserService
import com.ralphdugue.arcadephitogrpc.services.DeveloperService
import org.koin.dsl.module

val config = module {
    single { ArcadePhitoConfigFactory.create() }
}

val database = module {
    single<SqlDriver> { DatabaseFactory.create(get()) }
    single { ArcadePhito(get()) }
}

val repositories = module {
    single<DeveloperRepository> { DeveloperRepositoryImpl(get()) }
    single<AppUserRepository> { AppUserRepositoryImpl(get()) }
}

val useCases = module {
    single { CreateDeveloper(get()) }
    single { ValidateDeveloper(get()) }
    single { GenerateDeveloperToken(get()) }
    single { RegisterAppUser(get()) }
    single { VerifyLoginAttempt(get()) }
    single { VerifyDeveloperToken(get()) }
}

val services = module {
    single { DeveloperService(get(), get(), get()) }
    single { AppUserService(get(), get(), get()) }
}
