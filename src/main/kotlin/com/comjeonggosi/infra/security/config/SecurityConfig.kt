package com.comjeonggosi.infra.security.config

import com.comjeonggosi.infra.frontend.config.FrontendProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import reactor.core.publisher.Mono
import java.net.URI

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val frontendProperties: FrontendProperties
) {
    companion object {
        private const val ADMIN = "ADMIN"
        private const val USER = "USER"
    }

    @Bean
    fun roleHierarchy(): RoleHierarchy = RoleHierarchyImpl.fromHierarchy("$ADMIN > $USER")

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOriginPatterns = listOf("*")
        config.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true
        config.maxAge = 3600
        config.exposedHeaders = listOf("Authorization")

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
    }

    @Bean
    fun successHandler() = ServerAuthenticationSuccessHandler { exchange, authentication ->
        val response = exchange.exchange.response
        response.statusCode = HttpStatus.FOUND
        response.headers.location = URI.create(frontendProperties.successUrl)

        Mono.empty()
    }

    @Bean
    fun failureHandler() = ServerAuthenticationFailureHandler { exchange, _ ->
        val response = exchange.exchange.response
        response.statusCode = HttpStatus.FOUND
        response.headers.location = URI.create(frontendProperties.failureUrl)

        Mono.empty()
    }

    @Bean
    fun configure(http: ServerHttpSecurity) = http
        .csrf { it.disable() }
        .cors { it.configurationSource(corsConfigurationSource()) }
        .httpBasic { it.disable() }
        .formLogin { it.disable() }
        .logout { it.disable() }
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .authorizeExchange { exchanges ->
            exchanges
                // TODO("Add specific paths that should be publicly accessible")
                .pathMatchers("/auth/**").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
        }
        .oauth2Login { it
            .authenticationSuccessHandler(successHandler())
            .authenticationFailureHandler(failureHandler())
        }
        .build()
}