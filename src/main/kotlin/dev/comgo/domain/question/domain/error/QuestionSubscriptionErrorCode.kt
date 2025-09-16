package dev.comgo.domain.question.domain.error

import dev.comgo.common.exception.error.CustomErrorCode
import org.springframework.http.HttpStatus

enum class QuestionSubscriptionErrorCode(override val status: HttpStatus, override val message: String) :
    CustomErrorCode {
    QUESTION_SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 질문 구독입니다."),
}