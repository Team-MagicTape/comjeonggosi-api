package com.comjeonggosi.domain.quiz.application.service

import com.comjeonggosi.common.exception.CustomException
import com.comjeonggosi.domain.category.domain.error.CategoryErrorCode
import com.comjeonggosi.domain.category.domain.repository.CategoryRepository
import com.comjeonggosi.domain.category.presentation.dto.response.CategoryResponse
import com.comjeonggosi.domain.quiz.domain.document.QuizDocument
import com.comjeonggosi.domain.quiz.domain.entity.SubmissionEntity
import com.comjeonggosi.domain.quiz.domain.error.QuizErrorCode
import com.comjeonggosi.domain.quiz.domain.repository.QuizQueryRepository
import com.comjeonggosi.domain.quiz.domain.repository.QuizRepository
import com.comjeonggosi.domain.quiz.domain.repository.SubmissionRepository
import com.comjeonggosi.domain.quiz.presentation.dto.request.SolveQuizRequest
import com.comjeonggosi.domain.quiz.presentation.dto.response.QuizResponse
import com.comjeonggosi.domain.quiz.presentation.dto.response.QuizSubmissionResponse
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
    private val submissionRepository: SubmissionRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend fun getRandomQuiz(categoryId: Long?): QuizResponse {
        val quiz = quizQueryRepository.findRandomQuiz(categoryId)
            ?: throw CustomException(QuizErrorCode.PREPARING_QUIZ)
        return quiz.toResponse()
    }

    suspend fun solve(quizId: String, request: SolveQuizRequest): SolveQuizResponse {
        val quiz = quizRepository.findById(quizId) ?: throw CustomException(QuizErrorCode.QUIZ_NOT_FOUND)
        val isCorrect = quiz.answer == request.answer

        if (securityHolder.isAuthenticated()) {
            val user = securityHolder.getUser()
            val submission = SubmissionEntity(
                userId = user.id!!,
                quizId = quiz.id!!,
                answer = request.answer,
                isCorrected = isCorrect,
            )
            submissionRepository.save(submission)
        }

        return SolveQuizResponse(
            isCorrect = isCorrect,
            answer = quiz.answer
        )
    }

    suspend fun getMySubmissions(page: Int, size: Int, isCorrected: Boolean?): Flow<QuizSubmissionResponse> {
        val user = securityHolder.getUser()
        val offset = page.toLong() * size
        val submissions = if (isCorrected == null) {
            submissionRepository.findAllByUserId(user.id!!, size, offset)
        } else {
            submissionRepository.findAllByUserIdAndIsCorrected(user.id!!, isCorrected, size, offset)
        }

        return submissions.map {
            val quiz = quizRepository.findById(it.quizId) ?: throw CustomException(QuizErrorCode.QUIZ_NOT_FOUND)

            QuizSubmissionResponse(
                quiz = quiz.toResponse(),
                isCorrected = it.isCorrected,
                userAnswer = it.answer,
                submittedAt = it.createdAt
            )
        }
    }

    private suspend fun QuizDocument.toResponse(): QuizResponse {
        val category = categoryRepository.findById(this.categoryId)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        return QuizResponse(
            id = this.id!!,
            content = this.content,
            answer = this.answer,
            options = this.options,
            category = CategoryResponse(
                id = category.id!!,
                name = category.name,
                description = category.description,
            ),
            articleId = this.articleId
        )
    }
}