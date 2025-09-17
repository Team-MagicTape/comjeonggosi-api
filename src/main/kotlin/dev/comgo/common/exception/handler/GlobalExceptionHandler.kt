package dev.comgo.common.exception.handler

import dev.comgo.common.exception.CustomException
import dev.comgo.common.exception.response.ErrorResponse
import dev.comgo.infra.slack.service.SlackWebhookService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
class GlobalExceptionHandler(
    private val slackWebhookService: SlackWebhookService
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(
        ex: CustomException,
        exchange: ServerWebExchange
    ) = handleError(
        exception = ex,
        status = ex.status,
        code = ex.code,
        message = ex.message,
        path = exchange.request.uri.path
    )

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationException(
        ex: WebExchangeBindException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
            .ifEmpty { "입력값 검증 실패" }

        val validationErrors = ex.bindingResult.fieldErrors.associate {
            it.field to it.defaultMessage
        }

        return handleError(
            exception = ex,
            status = HttpStatus.BAD_REQUEST.value(),
            code = "VALIDATION_ERROR",
            message = message,
            path = exchange.request.uri.path,
            additionalInfo = mapOf("validationErrors" to validationErrors)
        )
    }

    @ExceptionHandler(
        IllegalArgumentException::class,
        ServerWebInputException::class,
        AccessDeniedException::class,
        Exception::class
    )
    fun handleGeneralException(
        ex: Exception,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        val (status, code, message) = mapException(ex)

        return handleError(
            exception = ex,
            status = status.value(),
            code = code,
            message = message,
            path = exchange.request.uri.path
        )
    }

    private fun handleError(
        exception: Throwable,
        status: Int,
        code: String,
        message: String,
        path: String,
        additionalInfo: Map<String, Any?> = emptyMap()
    ): ResponseEntity<ErrorResponse> {
        sendSlackNotification(exception, code, path, additionalInfo)
        return ResponseEntity
            .status(status)
            .body(ErrorResponse(status, code, message, path))
    }

    private fun sendSlackNotification(
        exception: Throwable,
        code: String,
        path: String,
        additionalInfo: Map<String, Any?>
    ) {
        scope.launch {
            slackWebhookService.sendError(exception, code, path, additionalInfo)
        }
    }

    private fun mapException(ex: Exception) = when (ex) {
        is IllegalArgumentException -> Triple(
            HttpStatus.BAD_REQUEST,
            "INVALID_ARGUMENT",
            ex.message ?: "잘못된 인자"
        )
        is ServerWebInputException -> Triple(
            HttpStatus.BAD_REQUEST,
            "INVALID_INPUT",
            "잘못된 입력"
        )
        is AccessDeniedException -> Triple(
            HttpStatus.FORBIDDEN,
            "ACCESS_DENIED",
            "접근 권한 없음"
        )
        else -> Triple(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_ERROR",
            "서버 오류"
        )
    }
}