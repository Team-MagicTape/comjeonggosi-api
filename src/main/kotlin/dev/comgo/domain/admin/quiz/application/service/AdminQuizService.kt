package dev.comgo.domain.admin.quiz.application.service

import dev.comgo.common.exception.CustomException
import dev.comgo.domain.admin.quiz.presentation.request.dto.CreateQuizRequest
import dev.comgo.domain.admin.quiz.presentation.request.dto.UpdateQuizRequest
import dev.comgo.domain.article.domain.error.ArticleErrorCode
import dev.comgo.domain.article.domain.repository.ArticleRepository
import dev.comgo.domain.category.domain.error.CategoryErrorCode
import dev.comgo.domain.category.domain.repository.CategoryRepository
import dev.comgo.domain.category.presentation.dto.response.CategoryResponse
import dev.comgo.domain.quiz.domain.document.QuizDocument
import dev.comgo.domain.quiz.domain.error.QuizErrorCode
import dev.comgo.domain.quiz.domain.repository.QuizRepository
import dev.comgo.domain.quiz.presentation.dto.response.QuizResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AdminQuizService(
    private val quizRepository: QuizRepository,
    private val categoryRepository: CategoryRepository,
    private val articleRepository: ArticleRepository
) {
    suspend fun createQuiz(request: CreateQuizRequest) {
        val category = categoryRepository.findById(request.categoryId)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        val article = request.articleId?.let { id ->
            articleRepository.findById(id)
                ?: throw CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND)
        }

        val quiz = QuizDocument(
            content = request.content,
            answer = request.answer,
            options = request.options ?: emptyList(),
            categoryId = category.id!!,
            articleId = article?.id,
            type = request.type,
            difficulty = request.difficulty,
            explanation = request.explanation,
            imageUrl = request.imageUrl,
        )

        quizRepository.save(quiz)
    }

    suspend fun getQuizzes(categoryId: Long?): Flow<QuizResponse> {
        val quizzes = if (categoryId != null) {
            quizRepository.findAllByCategoryIdAndDeletedAtIsNull(categoryId)
        } else {
            quizRepository.findAllByDeletedAtIsNull()
        }
        return quizzes.map { it.toResponse() }
    }

    suspend fun updateQuiz(quizId: String, request: UpdateQuizRequest) {
        val quiz = quizRepository.findById(quizId) ?: throw CustomException(QuizErrorCode.QUIZ_NOT_FOUND)

        val category = request.categoryId?.let {
            categoryRepository.findById(request.categoryId)
                ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)
        }

        val article = request.articleId?.let { id ->
            articleRepository.findById(id)
                ?: throw CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND)
        }

        quizRepository.save(
            quiz.copy(
                content = request.content ?: quiz.content,
                answer = request.answer ?: quiz.answer,
                options = request.options ?: quiz.options,
                categoryId = category?.id ?: quiz.categoryId,
                articleId = article?.id ?: quiz.articleId,
                type = request.type ?: quiz.type,
                difficulty = request.difficulty ?: quiz.difficulty
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
            articleId = this.articleId,
            type = this.type,
            difficulty = this.difficulty,
            explanation = this.explanation,
            imageUrl = this.imageUrl
        )
    }
}