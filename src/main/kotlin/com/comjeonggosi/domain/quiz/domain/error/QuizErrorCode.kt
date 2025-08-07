package com.comjeonggosi.domain.quiz.domain.error

import com.comjeonggosi.common.exception.error.CustomErrorCode
import org.springframework.http.HttpStatus

enum class QuizErrorCode(override val status: HttpStatus, override val message: String) : CustomErrorCode {
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 퀴즈입니다."),
    PREPARING_QUIZ(HttpStatus.NOT_FOUND, "퀴즈를 준비중입니다.")
}