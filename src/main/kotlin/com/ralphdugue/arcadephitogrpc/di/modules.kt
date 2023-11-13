package com.ralphdugue.arcadephitogrpc.di

import app.cash.sqldelight.db.SqlDriver
import com.ralphdugue.arcadephitogrpc.ArcadePhitoDB
import com.ralphdugue.arcadephitogrpc.adapters.DatabaseFactory
import com.ralphdugue.arcadephitogrpc.adapters.appusers.AppUserRepositoryImpl
import com.ralphdugue.arcadephitogrpc.adapters.config.ConfigFactory
import com.ralphdugue.arcadephitogrpc.adapters.config.ConfigRepositoryImpl
import com.ralphdugue.arcadephitogrpc.adapters.developers.DeveloperRepositoryImpl
import com.ralphdugue.arcadephitogrpc.adapters.security.SecurityRepositoryImpl
import com.ralphdugue.arcadephitogrpc.domain.appusers.AppUserRepository
import com.ralphdugue.arcadephitogrpc.domain.appusers.usecases.RegisterAppUser
import com.ralphdugue.arcadephitogrpc.domain.appusers.usecases.RetrieveAppUser
import com.ralphdugue.arcadephitogrpc.domain.appusers.usecases.VerifyLoginAttempt
import com.ralphdugue.arcadephitogrpc.domain.config.ConfigRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.DeveloperRepository
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.CreateDeveloper
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.GenerateDeveloperToken
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.ValidateDeveloper
import com.ralphdugue.arcadephitogrpc.domain.developers.usecases.VerifyDeveloperToken
import com.ralphdugue.arcadephitogrpc.domain.security.SecurityRepository
import com.ralphdugue.arcadephitogrpc.services.appuser.AppUserService
import com.ralphdugue.arcadephitogrpc.services.DeveloperService
import com.ralphdugue.arcadephitogrpc.services.ServiceInterceptor
import org.koin.dsl.module
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

val config = module {
    single { BCryptPasswordEncoder(16) }
    single { ConfigFactory.loadConfig() }
}

val database = module {
    single<SqlDriver> { DatabaseFactory.create(get()) }
    single { ArcadePhitoDB(get()) }
}

val repositories = module {
    single<SecurityRepository> { SecurityRepositoryImpl(get()) }
    single<DeveloperRepository> { DeveloperRepositoryImpl(get()) }
    single<ConfigRepository> { ConfigRepositoryImpl(get(), get(), get(), get()) }
    single<AppUserRepository> { AppUserRepositoryImpl(get()) }
}

val useCases = module {
    single { CreateDeveloper(get(), get()) }
    single { ValidateDeveloper(get(), get()) }
    single { GenerateDeveloperToken(get()) }
    single { RegisterAppUser(get(), get()) }
    single { VerifyLoginAttempt(get(), get()) }
    single { VerifyDeveloperToken(get(), get(), get()) }
    single { RetrieveAppUser(get()) }
}

val interceptors = module {
    single { ServiceInterceptor(get()) }
}

val services = module {
    single { DeveloperService(get(), get(), get()) }
    single { AppUserService(get(), get()) }
}
