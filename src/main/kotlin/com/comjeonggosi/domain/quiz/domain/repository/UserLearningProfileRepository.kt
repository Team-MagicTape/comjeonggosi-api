package com.comjeonggosi.domain.quiz.domain.repository

import com.comjeonggosi.domain.quiz.domain.entity.UserLearningProfileEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserLearningProfileRepository : CoroutineCrudRepository<UserLearningProfileEntity, Long> {
    suspend fun findByUserId(userId: Long): UserLearningProfileEntity?
}