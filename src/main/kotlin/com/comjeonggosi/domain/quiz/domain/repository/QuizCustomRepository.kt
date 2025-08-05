package com.comjeonggosi.domain.quiz.domain.repository

import com.comjeonggosi.domain.quiz.domain.document.QuizDocument
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Repository

@Repository
class QuizCustomRepository(
    private val mongoTemplate: ReactiveMongoTemplate
) {
    suspend fun findRandomQuiz(categoryId: String?): QuizDocument? {
        val criteria = Criteria.where("deletedAt").`is`(null)
        if (categoryId != null) {
            criteria.and("categoryId").`is`(categoryId)
        }
        val match: MatchOperation = Aggregation.match(criteria)
        val sample = Aggregation.sample(1)
        val aggregation = Aggregation.newAggregation(match, sample)
        return mongoTemplate.aggregate(aggregation, "quizzes", QuizDocument::class.java).awaitFirstOrNull()
    }
}
