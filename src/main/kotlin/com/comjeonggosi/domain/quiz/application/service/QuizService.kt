package com.comjeonggosi.domain.quiz.application.service

import com.comjeonggosi.common.exception.CustomException
import com.comjeonggosi.domain.quiz.domain.document.QuizDocument
import com.comjeonggosi.domain.quiz.domain.entity.SubmitEntity
import com.comjeonggosi.domain.quiz.domain.error.QuizErrorCode
import com.comjeonggosi.domain.quiz.domain.repository.QuizQueryRepository
import com.comjeonggosi.domain.quiz.domain.repository.QuizRepository
import com.comjeonggosi.domain.quiz.domain.repository.SubmitRepository
import com.comjeonggosi.domain.quiz.presentation.dto.request.SolveQuizRequest
import com.comjeonggosi.domain.quiz.presentation.dto.response.QuizResponse
import com.comjeonggosi.domain.quiz.presentation.dto.response.QuizSubmitResponse
import com.comjeonggosi.domain.quiz.presentation.dto.response.SolveQuizResponse
import com.comjeonggosi.infra.security.holder.SecurityHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val quizQueryRepository: QuizQueryRepository,
    private val securityHolder: SecurityHolder,
    private val submitRepository: SubmitRepository
) {
    suspend fun getRandomQuiz(categoryId: String?): QuizResponse {
        val quiz = quizQueryRepository.findRandomQuiz(categoryId)
            ?: throw CustomException(QuizErrorCode.PREPARING_QUIZ)
        return quiz.toResponse()
    }

    suspend fun solve(quizId: String, request: SolveQuizRequest): SolveQuizResponse {
        val quiz = quizRepository.findById(quizId) ?: throw CustomException(QuizErrorCode.QUIZ_NOT_FOUND)
        val isCorrect = quiz.answer == request.answer
        val user = securityHolder.getUserOrNull()

        if (user != null) {
            val submit = SubmitEntity(
                userId = user.id!!,
                quizId = quiz.id!!,
                answer = request.answer,
                isCorrected = isCorrect,
            )
            submitRepository.save(submit)
        }

        return SolveQuizResponse(
            isCorrect = isCorrect,
            answer = quiz.answer
        )
    }

    suspend fun getMySubmits(): Flow<QuizSubmitResponse> {
        val user = securityHolder.getUser()
        val submits = submitRepository.findAllByUserId(user.id!!)

        return submits.map {
            val quiz = quizRepository.findById(it.quizId) ?: throw CustomException(QuizErrorCode.QUIZ_NOT_FOUND)
            QuizSubmitResponse(
                quiz = quiz.toResponse(),
                isCorrected = it.isCorrected,
                userAnswer = it.answer
            )
        }
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