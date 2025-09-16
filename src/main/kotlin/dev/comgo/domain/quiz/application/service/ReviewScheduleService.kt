package dev.comgo.domain.quiz.application.service

import dev.comgo.domain.quiz.domain.entity.QuizReviewScheduleEntity
import dev.comgo.domain.quiz.domain.repository.QuizReviewScheduleRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class ReviewScheduleService(
    private val reviewScheduleRepository: QuizReviewScheduleRepository
) {
    companion object {
        private val INTERVALS = listOf(1L, 3L, 7L, 14L, 30L, 60L, 90L)
        private const val MASTERY_THRESHOLD = 0.9
        private const val MASTERY_COUNT = 5
    }

    suspend fun updateSchedule(
        userId: Long,
        quizId: String,
        isCorrect: Boolean
    ) {
        val schedule = reviewScheduleRepository.findByUserIdAndQuizId(userId, quizId)
            ?: createNewSchedule(userId, quizId)

        val updatedSchedule = calculateNextSchedule(schedule, isCorrect)
        reviewScheduleRepository.save(updatedSchedule)
    }

    suspend fun getDueReviews(
        userId: Long,
        limit: Int = 20
    ): List<String> {
        return reviewScheduleRepository
            .findByUserIdAndNextReviewAtBefore(userId, Instant.now())
            .map { it.quizId }
            .toList()
            .take(limit)
    }

    private fun createNewSchedule(
        userId: Long,
        quizId: String
    ): QuizReviewScheduleEntity {
        return QuizReviewScheduleEntity(
            userId = userId,
            quizId = quizId,
            nextReviewAt = Instant.now().plus(INTERVALS[0], ChronoUnit.DAYS)
        )
    }

    private fun calculateNextSchedule(
        schedule: QuizReviewScheduleEntity,
        isCorrect: Boolean
    ): QuizReviewScheduleEntity {
        val newCount = if (isCorrect) schedule.reviewCount + 1 else 0
        val newScore = updateRetentionScore(schedule.retentionScore, isCorrect)
        val intervalDays = getInterval(newCount, isCorrect)

        return schedule.copy(
            reviewCount = newCount,
            lastReviewedAt = Instant.now(),
            nextReviewAt = Instant.now().plus(intervalDays, ChronoUnit.DAYS),
            retentionScore = newScore,
            isMastered = isMastered(newScore, newCount)
        )
    }

    private fun updateRetentionScore(
        currentScore: Double,
        isCorrect: Boolean
    ): Double {
        return if (isCorrect) {
            (currentScore + 0.1).coerceAtMost(1.0)
        } else {
            (currentScore * 0.5).coerceAtLeast(0.0)
        }
    }

    private fun getInterval(reviewCount: Int, isCorrect: Boolean): Long {
        if (!isCorrect) return INTERVALS[0]

        val index = reviewCount.coerceAtMost(INTERVALS.size - 1)
        return INTERVALS[index]
    }

    private fun isMastered(
        retentionScore: Double,
        reviewCount: Int
    ): Boolean {
        return retentionScore > MASTERY_THRESHOLD && reviewCount > MASTERY_COUNT
    }
}