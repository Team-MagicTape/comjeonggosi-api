package dev.comgo.domain.quiz.application.service

import dev.comgo.common.exception.CustomException
import dev.comgo.domain.category.domain.error.CategoryErrorCode
import dev.comgo.domain.category.domain.repository.CategoryRepository
import dev.comgo.domain.category.presentation.dto.response.CategoryResponse
import dev.comgo.domain.quiz.application.service.RecommendationService.RecommendationType
import dev.comgo.domain.quiz.domain.document.QuizDocument
import dev.comgo.domain.quiz.domain.entity.SubmissionEntity
import dev.comgo.domain.quiz.domain.entity.UserLearningProfileEntity
import dev.comgo.domain.quiz.domain.enums.QuizMode
import dev.comgo.domain.quiz.domain.error.QuizErrorCode
import dev.comgo.domain.quiz.domain.repository.*
import dev.comgo.domain.quiz.presentation.dto.request.SolveQuizRequest
import dev.comgo.domain.quiz.presentation.dto.response.QuizResponse
import dev.comgo.domain.quiz.presentation.dto.response.QuizSubmissionResponse
import dev.comgo.domain.quiz.presentation.dto.response.SolveQuizResponse
import dev.comgo.infra.security.holder.SecurityHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val quizQueryRepository: QuizQueryRepository,
    private val submissionRepository: SubmissionRepository,
    private val categoryRepository: CategoryRepository,
    private val userLearningProfileRepository: UserLearningProfileRepository,
    private val userCategoryScoreRepository: UserCategoryScoreRepository,
    private val recommendationService: RecommendationService,
    private val reviewScheduleService: ReviewScheduleService,
    private val sessionService: SessionService,
    private val securityHolder: SecurityHolder
) {

    suspend fun getQuiz(
        categoryId: Long? = null,
        mode: QuizMode = QuizMode.RANDOM,
        difficulty: Int? = null,
        hideSolved: Boolean = true
    ): QuizResponse {
        val userId = getCurrentUserId()
        val sessionKey = sessionService.createSessionKey(userId)
        val recentIds = sessionService.getRecentIds(sessionKey)

        val hiddenIds = buildHiddenIds(userId, recentIds, hideSolved)

        val quiz = selectQuizByMode(
            mode = mode,
            userId = userId,
            categoryId = categoryId,
            difficulty = difficulty,
            hiddenIds = hiddenIds
        ) ?: throw CustomException(QuizErrorCode.QUIZ_PREPARING)

        return quiz.toResponse()
    }

    suspend fun getQuiz(quizId: String): QuizResponse {
        val quiz = quizRepository.findById(quizId) ?: throw CustomException(QuizErrorCode.QUIZ_NOT_FOUND)
        return quiz.toResponse()
    }

    suspend fun solve(quizId: String, request: SolveQuizRequest): SolveQuizResponse {
        val quiz = quizRepository.findById(quizId)
            ?: throw CustomException(QuizErrorCode.QUIZ_NOT_FOUND)

        val isCorrect = quiz.answer == request.answer

        val userId = getCurrentUserId()

        userId?.let { userId ->
            processSubmission(
                userId = userId,
                quizId = quizId,
                answer = request.answer,
                isCorrect = isCorrect,
                categoryId = quiz.categoryId
            )
        }

        val sessionKey = sessionService.createSessionKey(userId)
        if (isCorrect) sessionService.addToSession(sessionKey, quiz.id!!)

        return SolveQuizResponse(
            isCorrect = isCorrect,
            answer = quiz.answer
        )
    }

    suspend fun getMySubmissions(
        page: Int,
        size: Int,
        isCorrected: Boolean?
    ): Flow<QuizSubmissionResponse> {
        val userId = getCurrentUserId()
            ?: throw CustomException(QuizErrorCode.QUIZ_AUTHENTICATION_REQUIRED)

        return submissionRepository
            .findByUserId(userId, size, page.toLong() * size, isCorrected)
            .map { submission ->
                val quiz = quizRepository.findById(submission.quizId)
                    ?: throw CustomException(QuizErrorCode.QUIZ_NOT_FOUND)

                QuizSubmissionResponse(
                    quiz = quiz.toResponse(),
                    isCorrected = submission.isCorrected,
                    userAnswer = submission.answer,
                    submittedAt = submission.createdAt
                )
            }
    }

    private suspend fun buildHiddenIds(
        userId: Long?,
        recentIds: Set<String>,
        hideSolved: Boolean
    ): List<String> {
        if (userId == null || !hideSolved) {
            return recentIds.toList()
        }

        val solvedIds = submissionRepository.findRecentCorrectlySolvedIds(userId, 1000)

        return solvedIds + recentIds
    }

    private suspend fun selectQuizByMode(
        mode: QuizMode,
        userId: Long?,
        categoryId: Long?,
        difficulty: Int?,
        hiddenIds: List<String>
    ): QuizDocument? {
        return when (mode) {
            QuizMode.RECOMMEND -> getRecommendedQuiz(userId, categoryId, difficulty, hiddenIds)
            QuizMode.REVIEW -> getReviewQuiz(userId, hiddenIds)
            QuizMode.WEAKNESS -> getWeaknessQuiz(userId, categoryId, hiddenIds)
            QuizMode.RANDOM -> getRandomQuiz(categoryId, difficulty, hiddenIds)
        }
    }

    private suspend fun processSubmission(
        userId: Long,
        quizId: String,
        answer: String,
        isCorrect: Boolean,
        categoryId: Long
    ) {
        saveSubmission(userId, quizId, answer, isCorrect)
        updateProfile(userId, categoryId, isCorrect)
        reviewScheduleService.updateSchedule(userId, quizId, isCorrect)
    }

    private suspend fun getRecommendedQuiz(
        userId: Long?,
        categoryId: Long?,
        difficulty: Int?,
        hiddenIds: List<String>
    ): QuizDocument? {
        if (userId == null) {
            return getRandomQuiz(categoryId, difficulty, hiddenIds)
        }

        val candidates = quizQueryRepository.findQuizzesByCriteria(
            categoryIds = categoryId?.let { listOf(it) },
            difficulties = difficulty?.let { listOf(it) },
            hiddenIds = hiddenIds,
            limit = 200
        )

        return recommendationService.recommend(
            userId = userId,
            availableQuizzes = candidates,
            type = RecommendationType.BALANCED,
            limit = 1
        ).firstOrNull()
    }

    private suspend fun getReviewQuiz(
        userId: Long?,
        hiddenIds: List<String>
    ): QuizDocument? {
        if (userId == null) {
            return null
        }

        val dueQuizIds = reviewScheduleService
            .getDueReviews(userId)
            .filterNot { it in hiddenIds }

        return dueQuizIds.firstOrNull()?.let { quizRepository.findById(it) }
    }

    private suspend fun getWeaknessQuiz(
        userId: Long?,
        categoryId: Long?,
        hiddenIds: List<String>
    ): QuizDocument? {
        if (userId == null) {
            return null
        }

        val profile = userLearningProfileRepository.findByUserId(userId) ?: return null

        val categoryScores = userCategoryScoreRepository.findAllByProfileId(profile.id!!)
            .filter { categoryId == null || it.categoryId == categoryId }
            .sortedBy { it.score }
            .take(3)
            .map { it.categoryId }

        return quizQueryRepository.findQuizzesByCriteria(
            categoryIds = categoryScores,
            hiddenIds = hiddenIds,
            limit = 50
        ).randomOrNull()
    }

    private suspend fun getRandomQuiz(
        categoryId: Long?,
        difficulty: Int?,
        hiddenIds: List<String>
    ): QuizDocument? {
        val candidates = quizQueryRepository.findQuizzesByCriteria(
            categoryIds = categoryId?.let { listOf(it) },
            difficulties = difficulty?.let { listOf(it) },
            hiddenIds = hiddenIds,
            limit = 50
        )

        return candidates.randomOrNull()
    }

    private suspend fun saveSubmission(
        userId: Long,
        quizId: String,
        answer: String,
        isCorrect: Boolean
    ) {
        submissionRepository.save(
            SubmissionEntity(
                userId = userId,
                quizId = quizId,
                answer = answer,
                isCorrected = isCorrect
            )
        )
    }

    private suspend fun updateProfile(
        userId: Long,
        categoryId: Long,
        isCorrect: Boolean
    ) {
        var profile = userLearningProfileRepository.findByUserId(userId)

        if (profile == null) {
            profile = userLearningProfileRepository.save(
                UserLearningProfileEntity(userId = userId)
            )
        }

        val profileId = profile.id!!

        // Get current category score
        val categoryScores = userCategoryScoreRepository.findAllByProfileId(profileId)
        val currentScore = categoryScores.find { it.categoryId == categoryId }?.score ?: 0.5
        val newScore = calculateNewScore(currentScore, isCorrect)

        // Update category score
        userCategoryScoreRepository.upsert(profileId, categoryId, newScore)

        // Update profile stats
        userLearningProfileRepository.save(
            profile.copy(
                totalSolved = profile.totalSolved + 1,
                totalCorrect = profile.totalCorrect + if (isCorrect) 1 else 0,
                lastStudyDate = Instant.now()
            )
        )
    }

    private fun calculateNewScore(currentScore: Double, isCorrect: Boolean): Double {
        return if (isCorrect) {
            (currentScore * 0.9 + 1.0 * 0.1).coerceAtMost(1.0)
        } else {
            (currentScore * 0.9).coerceAtLeast(0.0)
        }
    }

    private suspend fun QuizDocument.toResponse(): QuizResponse {
        val category = categoryRepository.findById(categoryId)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        return QuizResponse(
            id = id!!,
            content = content,
            answer = answer,
            options = (options + answer).shuffled(),
            category = CategoryResponse(
                id = category.id!!,
                name = category.name,
                description = category.description
            ),
            articleId = articleId,
            type = type,
            difficulty = difficulty,
            explanation = explanation,
            imageUrl = imageUrl,
        )
    }

    private suspend fun getCurrentUserId(): Long? {
        return when {
            securityHolder.isAuthenticated() -> securityHolder.getUser().id
            else -> null
        }
    }

}
