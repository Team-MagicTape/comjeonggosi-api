package com.comjeonggosi.domain.quiz.domain.repository

import com.comjeonggosi.domain.quiz.domain.entity.UserCategoryScoreEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserCategoryScoreRepository : CoroutineCrudRepository<UserCategoryScoreEntity, Long> {

    @Query("SELECT * FROM user_category_scores WHERE profile_id = :profileId")
    suspend fun findAllByProfileId(profileId: Long): List<UserCategoryScoreEntity>

    @Query(
        """
        INSERT INTO user_category_scores (profile_id, category_id, score) 
        VALUES (:profileId, :categoryId, :score)
        ON CONFLICT (profile_id, category_id) 
        DO UPDATE SET score = :score
    """
    )
    suspend fun upsert(profileId: Long, categoryId: Long, score: Double)
}