package com.comjeonggosi.infra.flyway.config

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(FlywayProperties::class)
class FlywayConfig(
    private val flywayProperties: FlywayProperties
) {
    @Bean
    fun flyway(): Flyway {
        val flyway = Flyway.configure()
            .baselineOnMigrate(flywayProperties.isBaselineOnMigrate)
            .baselineVersion(flywayProperties.baselineVersion)
            .dataSource(
                flywayProperties.url,
                flywayProperties.user,
                flywayProperties.password
            )
            .load()

        flyway.repair()
        flyway.migrate()

        return flyway
    }
}