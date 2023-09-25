package com.ralphdugue.arcadephitogrpc.adapters

import app.cash.sqldelight.driver.jdbc.JdbcDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

object DatabaseFactory {

    fun create(config: ArcadePhitoConfig): JdbcDriver {
        val config =HikariConfig().apply {
            jdbcUrl = config.db.host
            username = config.db.username
            password = config.db.password
            driverClassName = config.db.driver
        }
        val dataSource = HikariDataSource(config)
        return dataSource.asJdbcDriver()
    }
}