package dev.comgo.domain.user.domain.error

import dev.comgo.common.exception.error.CustomErrorCode
import org.springframework.http.HttpStatus

enum class UserErrorCode(override val status: HttpStatus, override val message: String) : CustomErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    USER_NOT_LOGGED_IN(HttpStatus.UNAUTHORIZED, "로그인된 유저가 아닙니다."),
}