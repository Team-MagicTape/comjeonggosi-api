package com.comjeonggosi.infra.security.jwt.filter

import com.comjeonggosi.infra.security.jwt.provider.JwtProvider
import com.comjeonggosi.logger
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider
) : WebFilter {
    private val log = logger()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val accessToken = extractToken(exchange)
        log.info("Extracted access token: $accessToken")
        return if (accessToken != null && jwtProvider.validateToken(accessToken)) {
            val userId = jwtProvider.getUserId(accessToken)
            val authentication = UsernamePasswordAuthenticationToken(userId, null, emptyList())
            chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
        } else {
            chain.filter(exchange)
        }
    }

    private fun extractToken(exchange: ServerWebExchange): String? {
        return exchange.request.cookies["accessToken"]?.firstOrNull()?.value
    }
}