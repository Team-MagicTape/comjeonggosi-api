package dev.comgo.common.exception.error

import org.springframework.http.HttpStatus

enum class GeneralErrorCode(override val status: HttpStatus, override val message: String) : CustomErrorCode {
    NO_RESOURCE_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리소스입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. (%s)"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
}
