package dev.comgo.domain.question.domain.error

import dev.comgo.common.exception.error.CustomErrorCode
import org.springframework.http.HttpStatus

enum class QuestionErrorCode(override val status: HttpStatus, override val message: String) : CustomErrorCode {
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 질문입니다."),
    QUESTION_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 질문입니다.")
}