package com.ralphdugue.arcadephitogrpc.adapters

import app.cash.sqldelight.driver.jdbc.JdbcDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.ralphdugue.arcadephitogrpc.domain.config.entities.ArcadePhitoConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

object DatabaseFactory {

    fun create(config: ArcadePhitoConfig, logger: KLogger = KotlinLogging.logger {}): JdbcDriver = try {
        logger.info { "Creating database driver with config: $config" }
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.db.host
            username = config.db.username
            password = config.db.password
            driverClassName = config.db.driver
        }
        val dataSource = HikariDataSource(hikariConfig)
        logger.info { "Hikari dataSource from ArcadePhitoConfig: $dataSource" }
        val driver = dataSource.asJdbcDriver()
        logger.info { "JDBC driver from Hikari dataSource: $driver" }
        driver
    } catch (e: Exception) {
        logger.error(e) { "Error creating database driver." }
        throw e
    }
}