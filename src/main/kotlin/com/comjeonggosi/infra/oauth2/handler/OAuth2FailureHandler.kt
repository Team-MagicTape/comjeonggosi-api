package com.comjeonggosi.infra.oauth2.handler

import com.comjeonggosi.infra.frontend.config.FrontendProperties
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Component
class OAuth2FailureHandler(
    private val frontendProperties: FrontendProperties
) : ServerAuthenticationFailureHandler {

    override fun onAuthenticationFailure(
        webFilterExchange: WebFilterExchange,
        ex: AuthenticationException
    ): Mono<Void> {
        val response = webFilterExchange.exchange.response

        val redirectUrl = UriComponentsBuilder
            .fromUriString(frontendProperties.baseUrl)
            .path("/auth/failure")
            .queryParam("error", ex.message)
            .build()
            .toUri()

        response.statusCode = HttpStatus.FOUND
        response.headers.location = redirectUrl

        return response.setComplete()
    }
}