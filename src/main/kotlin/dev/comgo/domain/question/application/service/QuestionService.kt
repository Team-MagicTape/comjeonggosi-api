package dev.comgo.domain.question.application.service

import dev.comgo.common.exception.CustomException
import dev.comgo.domain.category.domain.repository.CategoryRepository
import dev.comgo.domain.question.domain.entity.QuestionEntity
import dev.comgo.domain.question.domain.entity.QuestionSubscriptionCategoryEntity
import dev.comgo.domain.question.domain.entity.QuestionSubscriptionEntity
import dev.comgo.domain.question.domain.error.QuestionErrorCode
import dev.comgo.domain.question.domain.error.QuestionSubscriptionErrorCode
import dev.comgo.domain.question.domain.repository.QuestionDeliveryRepository
import dev.comgo.domain.question.domain.repository.QuestionRepository
import dev.comgo.domain.question.domain.repository.QuestionSubscriptionCategoryRepository
import dev.comgo.domain.question.domain.repository.QuestionSubscriptionRepository
import dev.comgo.domain.question.presentation.dto.request.SubscribeQuestionRequest
import dev.comgo.domain.question.presentation.dto.response.QuestionResponse
import dev.comgo.domain.question.presentation.dto.response.QuestionSubscriptionResponse
import dev.comgo.infra.security.holder.SecurityHolder
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.time.Instant

@Service
class QuestionService(
    private val questionSubscriptionCategoryRepository: QuestionSubscriptionCategoryRepository,
    private val questionDeliveryRepository: QuestionDeliveryRepository,
    private val questionRepository: QuestionRepository,
    private val securityHolder: SecurityHolder,
    private val questionSubscriptionRepository: QuestionSubscriptionRepository,
    private val transactionalOperator: TransactionalOperator,
    private val categoryRepository: CategoryRepository
) {
    suspend fun subscribe(request: SubscribeQuestionRequest) = transactionalOperator.executeAndAwait {
        val userId = securityHolder.getUserId()
        val existing = questionSubscriptionRepository.findByUserId(userId)
        val subscription = questionSubscriptionRepository.save(
            existing?.copy(hour = request.hour) ?: QuestionSubscriptionEntity(
                userId = userId,
                hour = request.hour,
                subscribedAt = Instant.now(),
                email = request.email
            )
        )

        val current = questionSubscriptionCategoryRepository.findAllBySubscriptionId(subscription.id!!)
        val currentIds = current.map { it.categoryId }.toSet()
        val newIds = request.categoryIds.toSet()

        val toAdd = (newIds - currentIds).map { categoryId ->
            QuestionSubscriptionCategoryEntity(
                subscriptionId = subscription.id,
                categoryId = categoryId
            )
        }
        if (toAdd.isNotEmpty())
            questionSubscriptionCategoryRepository.saveAll(toAdd).collect()

        val toRemove = current.filter { it.categoryId !in newIds }.toList()
        if (toRemove.isNotEmpty())
            questionSubscriptionCategoryRepository.deleteAll(toRemove)
    }

    suspend fun getSubscription(): QuestionSubscriptionResponse {
        val userId = securityHolder.getUserId()
        val subscription = questionSubscriptionRepository.findByUserId(userId)
            ?: throw CustomException(QuestionSubscriptionErrorCode.QUESTION_SUBSCRIPTION_NOT_FOUND)

        return subscription.toResponse()
    }

    suspend fun getQuestions(categoryId: Long): Flow<QuestionResponse> {
        val userId = securityHolder.getUserId()
        val day = questionDeliveryRepository
            .findTopByUserIdAndCategoryIdOrderByDayDesc(userId, categoryId)?.day ?: return emptyFlow()

        return questionRepository
            .findAllByCategoryIdAndDayLessThanEqual(categoryId, day)
            .map { it.toResponse() }
    }

    suspend fun getQuestion(questionId: Long): QuestionResponse {
        val userId = securityHolder.getUserId()
        val question = questionRepository.findById(questionId)
            ?: throw CustomException(QuestionErrorCode.QUESTION_NOT_FOUND)

        val subscription = questionSubscriptionRepository.findByUserId(userId)
            ?: throw CustomException(QuestionSubscriptionErrorCode.QUESTION_SUBSCRIPTION_NOT_FOUND)

        if (!questionSubscriptionCategoryRepository
                .existsBySubscriptionIdAndCategoryId(
                    subscription.id!!,
                    question.categoryId
                )
        ) throw CustomException(QuestionErrorCode.QUESTION_NOT_FOUND)

        val maxDay = questionDeliveryRepository
            .findTopByUserIdAndCategoryIdOrderByDayDesc(userId, question.categoryId)?.day ?: 0L

        if (question.day > maxDay) {
            throw CustomException(QuestionErrorCode.QUESTION_NOT_FOUND)
        }

        return question.toResponse()
    }

    private fun QuestionEntity.toResponse() = QuestionResponse(
        id = id!!,
        day = day,
        categoryId = categoryId,
        title = title,
        content = content,
        answer = answer
    )

    private suspend fun QuestionSubscriptionEntity.toResponse(): QuestionSubscriptionResponse {
        val categories = questionSubscriptionCategoryRepository
            .findAllBySubscriptionId(id!!)
            .map { (_, _, categoryId) ->
                categoryRepository.findById(categoryId)!!.let {
                    QuestionSubscriptionResponse.Category(
                        id = it.id!!,
                        name = it.name
                    )
                }
            }
            .toList()

        return QuestionSubscriptionResponse(
            hour = hour,
            categories = categories,
            email = email ?: securityHolder.getUser().email
        )
    }
}