package dev.comgo.domain.quiz.domain.repository

import dev.comgo.domain.quiz.domain.entity.UserLearningProfileEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserLearningProfileRepository : CoroutineCrudRepository<UserLearningProfileEntity, Long> {
    suspend fun findByUserId(userId: Long): UserLearningProfileEntity?
}