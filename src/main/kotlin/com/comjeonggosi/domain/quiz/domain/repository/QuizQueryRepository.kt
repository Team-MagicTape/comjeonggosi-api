package com.comjeonggosi.domain.quiz.domain.repository

import com.comjeonggosi.domain.quiz.domain.document.QuizDocument
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Repository

@Repository
class QuizQueryRepository(
    private val mongoTemplate: ReactiveMongoTemplate
) {
    suspend fun findRandomQuiz(
        categoryId: Long?,
        hiddenIds: List<String> = emptyList()
    ): QuizDocument? {
        val criteria = Criteria.where("deleted_at").`is`(null)

        if (categoryId != null) {
            criteria.and("category_id").`is`(categoryId)
        }

        if (hiddenIds.isNotEmpty()) {
            criteria.and("_id").nin(hiddenIds)
        }

        val match: MatchOperation = Aggregation.match(criteria)
        val sample = Aggregation.sample(1)
        val aggregation = Aggregation.newAggregation(match, sample)
        return mongoTemplate.aggregate(aggregation, "quizzes", QuizDocument::class.java).awaitFirstOrNull()
    }

    suspend fun findQuizzesByCriteria(
        categoryIds: List<Long>? = null,
        difficulties: List<Int>? = null,
        hiddenIds: List<String> = emptyList(),
        limit: Int = 100
    ): List<QuizDocument> {
        val criteria = Criteria.where("deleted_at").`is`(null)

        if (!categoryIds.isNullOrEmpty()) {
            criteria.and("category_id").`in`(categoryIds)
        }

        if (!difficulties.isNullOrEmpty()) {
            criteria.and("difficulty").`in`(difficulties)
        }

        if (hiddenIds.isNotEmpty()) {
            criteria.and("_id").nin(hiddenIds)
        }

        val match: MatchOperation = Aggregation.match(criteria)
        val sample = Aggregation.sample(limit.toLong())
        val aggregation = Aggregation.newAggregation(match, sample)

        return mongoTemplate.aggregate(aggregation, "quizzes", QuizDocument::class.java)
            .collectList()
            .awaitFirstOrDefault(emptyList())
    }
}
