package dev.comgo.common.exception.handler

import dev.comgo.common.exception.CustomException
import dev.comgo.common.exception.response.ErrorResponse
import dev.comgo.infra.slack.service.SlackWebhookService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
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
    ): ResponseEntity<ErrorResponse> {
        val path = exchange.request.uri.path
        return ResponseEntity
            .status(ex.status)
            .body(ErrorResponse(ex.status, ex.code, ex.message, path))
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationException(
        ex: WebExchangeBindException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        val path = exchange.request.uri.path
        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
            .ifEmpty { "입력값 검증 실패" }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                message,
                path
            ))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(
        ex: Exception,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        val path = exchange.request.uri.path

        sendSlackNotification(ex, "INTERNAL_ERROR", path)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "서버 오류가 발생했습니다",
                path
            ))
    }

    private fun sendSlackNotification(
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