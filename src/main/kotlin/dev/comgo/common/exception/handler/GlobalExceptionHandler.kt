package dev.comgo.common.exception.handler

import dev.comgo.common.exception.CustomException
import dev.comgo.common.exception.error.GeneralErrorCode
import dev.comgo.common.exception.response.ErrorResponse
import dev.comgo.infra.slack.service.SlackWebhookService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.resource.NoResourceFoundException
import org.springframework.web.server.ServerWebExchange

@RestControllerAdvice
class GlobalExceptionHandler(
    private val slackWebhookService: SlackWebhookService
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(
        ex: CustomException,
        exchange: ServerWebExchange
    ) = ex.toResponse(exchange.request.uri.path)

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(
        ex: WebExchangeBindException,
        exchange: ServerWebExchange
    ) = CustomException(GeneralErrorCode.INVALID_REQUEST, ex.bindingResult.allErrors.joinToString { it.defaultMessage ?: "Invalid value" })
        .toResponse(exchange.request.uri.path)

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(
        ex: NoResourceFoundException,
        exchange: ServerWebExchange
    ) = CustomException(GeneralErrorCode.NO_RESOURCE_FOUND).toResponse(exchange.request.uri.path)

    @ExceptionHandler(Exception::class)
    fun handleException(
        ex: Exception,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        val path = exchange.request.uri.path

        sendError(ex, "INTERNAL_ERROR", path)

        return CustomException(GeneralErrorCode.INTERNAL_SERVER_ERROR).toResponse(path)
    }

    private fun sendError(
        exception: Throwable,
        code: String,
        path: String,
        additionalInfo: Map<String, Any?> = emptyMap()
    ) {
        scope.launch {
            slackWebhookService.sendError(exception, code, path, additionalInfo)
        }
    }
}