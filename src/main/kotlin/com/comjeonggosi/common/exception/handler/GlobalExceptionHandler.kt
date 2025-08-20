package com.comjeonggosi.common.exception.handler

import com.comjeonggosi.common.exception.CustomException
import com.comjeonggosi.common.exception.response.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.server.UnsupportedMediaTypeStatusException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException, exchange: ServerWebExchange): ResponseEntity<ErrorResponse> {
        log.warn(ex.message, ex)

        val errorResponse = ErrorResponse(
            status = ex.status,
            code = ex.code,
            message = ex.message,
            path = exchange.request.uri.path
        )

        return ResponseEntity.status(ex.status).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        log.warn(ex.message, ex)

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            code = "INVALID_PARAMETER",
            message = ex.message ?: "잘못된 파라미터입니다.",
            path = exchange.request.uri.path
        )
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationException(
        ex: WebExchangeBindException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        log.warn(ex.message, ex)

        val errorMessage = ex.bindingResult.fieldErrors
            .joinToString(", ") { "{it.field}: {it.defaultMessage}" }

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            code = "VALIDATION_ERROR",
            message = errorMessage.ifEmpty { "입력값 검증에 실패했습니다." },
            path = exchange.request.uri.path
        )
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(
        ex: ServerWebInputException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        log.warn(ex.message, ex)

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            code = "INVALID_INPUT",
            message = "입력 형식이 올바르지 않습니다.",
            path = exchange.request.uri.path
        )
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(UnsupportedMediaTypeStatusException::class)
    fun handleUnsupportedMediaTypeException(
        ex: UnsupportedMediaTypeStatusException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        log.warn(ex.message, ex)

        val errorResponse = ErrorResponse(
            status = HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
            code = "UNSUPPORTED_MEDIA_TYPE",
            message = "지원하지 않는 미디어 타입입니다.",
            path = exchange.request.uri.path
        )
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        ex: AccessDeniedException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        log.warn(ex.message, ex)

        val errorResponse = ErrorResponse(
            status = HttpStatus.FORBIDDEN.value(),
            code = "ACCESS_DENIED",
            message = "접근 권한이 없습니다.",
            path = exchange.request.uri.path
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
    }

    @ExceptionHandler(RuntimeException::class)
    suspend fun handleRuntimeException(
        ex: RuntimeException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        log.error(ex.message, ex)

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            code = "INTERNAL_SERVER_ERROR",
            message = "서버 내부 오류가 발생했습니다.",
            path = exchange.request.uri.path
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, exchange: ServerWebExchange): ResponseEntity<ErrorResponse> {
        log.error(ex.message, ex)

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            code = "UNKNOWN_ERROR",
            message = "알 수 없는 오류가 발생했습니다.",
            path = exchange.request.uri.path
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}