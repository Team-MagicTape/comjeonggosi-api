package com.comjeonggosi.infra.r2dbc.config

import com.comjeonggosi.domain.user.domain.enums.UserRole
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.PostgresDialect

@Configuration
@EnableR2dbcAuditing
class R2dbcConfig {
    @Bean
    fun r2dbcCustomConversions(): R2dbcCustomConversions {
        return R2dbcCustomConversions.of(
            PostgresDialect.INSTANCE,
            StringRoleToUserRoleConverter(),
            UserRoleToStringRoleConverter()
        )
    }

    class StringRoleToUserRoleConverter : Converter<String, UserRole> {
        override fun convert(source: String): UserRole {
            return UserRole.valueOf(source.uppercase())
        }
    }

    class UserRoleToStringRoleConverter : Converter<UserRole, String> {
        override fun convert(source: UserRole): String {
            return source.name
        }
    }
}