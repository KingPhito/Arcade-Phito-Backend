package com.arcadephito.ralphdugue.backend.arcadephito.di

import com.arcadephito.ralphdugue.backend.arcadephito.controllers.UserController
import com.arcadephito.ralphdugue.backend.arcadephito.controllers.UserControllerImpl
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
val database = Database.connect(
    url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
    user = "root",
    driver = "org.h2.Driver",
    password = ""
)

val appModule = module {
    single {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "root",
            driver = "org.h2.Driver",
            password = ""
        )
    }
    single<UserController> { UserControllerImpl(get()) }
}