package com.comjeonggosi.domain.quiz.application.service

import com.comjeonggosi.common.exception.CustomException
import com.comjeonggosi.domain.quiz.domain.document.QuizDocument
import com.comjeonggosi.domain.quiz.domain.error.QuizErrorCode
import com.comjeonggosi.domain.quiz.domain.repository.QuizCustomRepository
import com.comjeonggosi.domain.quiz.domain.repository.QuizRepository
import com.comjeonggosi.domain.quiz.presentation.dto.response.QuizResponse
import org.springframework.stereotype.Service

@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val quizCustomRepository: QuizCustomRepository
) {
    suspend fun getRandomQuiz(categoryId: String?): QuizResponse {
        val quiz = quizCustomRepository.findRandomQuiz(categoryId)
            ?: throw CustomException(QuizErrorCode.PREPARING_QUIZ)
        return quiz.toResponse()
    }

    suspend fun solve(quizId: Long, userAnswer: String) {

    }

    private fun QuizDocument.toResponse() =
        QuizResponse(
            id = this.id!!,
            content = this.content,
            answer = this.answer,
            options = this.options,
            categoryId = this.categoryId.toLong()
        )
}