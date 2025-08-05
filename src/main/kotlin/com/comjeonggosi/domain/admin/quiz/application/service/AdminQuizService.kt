package com.comjeonggosi.domain.admin.quiz.application.service

import com.comjeonggosi.common.exception.CustomException
import com.comjeonggosi.domain.admin.quiz.presentation.request.dto.CreateQuizRequest
import com.comjeonggosi.domain.admin.quiz.presentation.request.dto.UpdateQuizRequest
import com.comjeonggosi.domain.quiz.domain.document.QuizDocument
import com.comjeonggosi.domain.quiz.domain.error.QuizErrorCode
import com.comjeonggosi.domain.quiz.domain.repository.QuizRepository
import com.comjeonggosi.domain.quiz.presentation.dto.response.QuizResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AdminQuizService(
    private val quizRepository: QuizRepository
) {
    suspend fun createQuiz(request: CreateQuizRequest) {
        val quiz = QuizDocument(
            content = request.content,
            answer = request.answer,
            options = request.options ?: emptyList(),
            categoryId = request.categoryId.toString()
        )

        quizRepository.save(quiz)
    }

    suspend fun getQuizzes(categoryId: String?): Flow<QuizResponse> {
        val quizzes = if (categoryId != null) {
            quizRepository.findAllByCategoryIdAndDeletedAtIsNull(categoryId)
        } else {
            quizRepository.findAllByDeletedAtIsNull()
        }
        return quizzes.map { it.toResponse() }
    }

    suspend fun updateQuiz(quizId: String, request: UpdateQuizRequest) {
        val quiz = quizRepository.findById(quizId) ?: throw CustomException(QuizErrorCode.QUIZ_NOT_FOUND)

        quizRepository.save(
            quiz.copy(
                content = request.content ?: quiz.content,
                answer = request.answer ?: quiz.answer,
                options = request.options ?: quiz.options,
                categoryId = request.categoryId?.toString() ?: quiz.categoryId
            )
        )
    }

    suspend fun deleteQuiz(quizId: String) {
        val quiz = quizRepository.findById(quizId) ?: throw CustomException(QuizErrorCode.QUIZ_NOT_FOUND)

        quizRepository.save(
            quiz.copy(
                deletedAt = Instant.now(),
            )
        )
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