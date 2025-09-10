package com.comjeonggosi.domain.quiz.application.service

import com.comjeonggosi.domain.quiz.domain.document.QuizDocument
import com.comjeonggosi.domain.quiz.domain.entity.UserLearningProfileEntity
import com.comjeonggosi.domain.quiz.domain.repository.UserCategoryScoreRepository
import com.comjeonggosi.domain.quiz.domain.repository.UserLearningProfileRepository
import org.springframework.stereotype.Service
import kotlin.math.abs
import kotlin.math.exp

@Service
class RecommendationService(
    private val userLearningProfileRepository: UserLearningProfileRepository,
    private val userCategoryScoreRepository: UserCategoryScoreRepository
) {
    companion object {
        private const val OPTIMAL_ACCURACY = 0.7
        private const val DIFFICULTY_WEIGHT = 0.4
        private const val CATEGORY_WEIGHT = 0.4
        private const val FRESHNESS_WEIGHT = 0.2
    }

    suspend fun recommend(
        userId: Long,
        availableQuizzes: List<QuizDocument>,
        type: RecommendationType = RecommendationType.BALANCED,
        limit: Int = 10
    ): List<QuizDocument> {
        if (availableQuizzes.isEmpty()) return emptyList()
        
        val profile = getCachedProfile(userId) 
            ?: return availableQuizzes.shuffled().take(limit)

        val categoryScores = userCategoryScoreRepository
            .findAllByProfileId(profile.id!!)
            .associate { it.categoryId to it.score }

        return availableQuizzes
            .map { quiz -> quiz to calculateScore(quiz, profile, categoryScores, type) }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }

    private suspend fun getCachedProfile(userId: Long): UserLearningProfileEntity? {
        return userLearningProfileRepository.findByUserId(userId)
    }

    private fun calculateScore(
        quiz: QuizDocument,
        profile: UserLearningProfileEntity,
        categoryScores: Map<Long, Double>,
        type: RecommendationType
    ): Double {
        val userAccuracy = calculateUserAccuracy(profile)
        
        val difficultyScore = calculateDifficultyScore(quiz.difficulty, userAccuracy)
        val categoryScore = calculateCategoryScore(quiz.categoryId, categoryScores)
        val freshnessScore = calculateFreshnessScore(quiz)
        
        return applyTypeWeights(type, difficultyScore, categoryScore, freshnessScore)
    }

    private fun calculateUserAccuracy(profile: UserLearningProfileEntity): Double {
        return if (profile.totalSolved > 0) {
            profile.totalCorrect.toDouble() / profile.totalSolved
        } else {
            OPTIMAL_ACCURACY
        }
    }

    private fun calculateDifficultyScore(
        quizDifficulty: Int,
        userAccuracy: Double
    ): Double {
        val optimalDifficulty = getOptimalDifficulty(userAccuracy)
        val distance = abs(quizDifficulty - optimalDifficulty).toDouble()
        
        // 거리가 가까울수록 높은 점수 (가우시안 분포)
        return exp(-0.5 * distance * distance)
    }

    private fun getOptimalDifficulty(userAccuracy: Double): Int {
        return when {
            userAccuracy > 0.85 -> 4  // 매우 잘함 → 어려운 문제
            userAccuracy > 0.70 -> 3  // 적당함 → 중간 문제
            userAccuracy > 0.55 -> 2  // 어려워함 → 쉬운 문제
            else -> 1                 // 매우 어려워함 → 매우 쉬운 문제
        }
    }

    private fun calculateCategoryScore(
        categoryId: Long,
        categoryScores: Map<Long, Double>
    ): Double {
        val categoryAccuracy = categoryScores[categoryId] ?: OPTIMAL_ACCURACY
        
        // 약한 카테고리일수록 높은 점수
        return 1.0 - categoryAccuracy
    }

    private fun calculateFreshnessScore(quiz: QuizDocument): Double {
        // 정답률이 극단적이지 않은 문제 선호
        return when {
            quiz.correctRate in 0.3..0.8 -> 1.0
            quiz.correctRate in 0.2..0.9 -> 0.8
            else -> 0.5
        }
    }

    private fun applyTypeWeights(
        type: RecommendationType,
        difficultyScore: Double,
        categoryScore: Double,
        freshnessScore: Double
    ): Double {
        return when (type) {
            RecommendationType.BALANCED -> {
                difficultyScore * DIFFICULTY_WEIGHT +
                categoryScore * CATEGORY_WEIGHT +
                freshnessScore * FRESHNESS_WEIGHT
            }
            RecommendationType.WEAKNESS -> {
                difficultyScore * 0.2 +
                categoryScore * 0.7 +
                freshnessScore * 0.1
            }
            RecommendationType.CHALLENGE -> {
                difficultyScore * 0.7 +
                categoryScore * 0.1 +
                freshnessScore * 0.2
            }
        }
    }

    enum class RecommendationType {
        BALANCED,   // 균형잡힌 추천
        WEAKNESS,   // 약점 집중
        CHALLENGE   // 도전 모드
    }
}