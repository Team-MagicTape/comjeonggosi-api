package com.comjeonggosi.domain.question.application.service

import com.comjeonggosi.common.exception.CustomException
import com.comjeonggosi.domain.category.domain.repository.CategoryRepository
import com.comjeonggosi.domain.question.domain.entity.QuestionDeliveryEntity
import com.comjeonggosi.domain.question.domain.entity.QuestionEntity
import com.comjeonggosi.domain.question.domain.entity.QuestionSubscriptionCategoryEntity
import com.comjeonggosi.domain.question.domain.entity.QuestionSubscriptionEntity
import com.comjeonggosi.domain.question.domain.error.QuestionErrorCode
import com.comjeonggosi.domain.question.domain.error.QuestionSubscriptionErrorCode
import com.comjeonggosi.domain.question.domain.repository.QuestionDeliveryRepository
import com.comjeonggosi.domain.question.domain.repository.QuestionRepository
import com.comjeonggosi.domain.question.domain.repository.QuestionSubscriptionCategoryRepository
import com.comjeonggosi.domain.question.domain.repository.QuestionSubscriptionRepository
import com.comjeonggosi.domain.question.presentation.dto.request.SubscribeQuestionRequest
import com.comjeonggosi.domain.question.presentation.dto.response.QuestionResponse
import com.comjeonggosi.domain.question.presentation.dto.response.QuestionSubscriptionResponse
import com.comjeonggosi.domain.user.domain.entity.UserEntity
import com.comjeonggosi.domain.user.domain.repository.UserRepository
import com.comjeonggosi.infra.email.service.EmailService
import com.comjeonggosi.infra.frontend.config.FrontendProperties
import com.comjeonggosi.infra.security.holder.SecurityHolder
import com.comjeonggosi.infra.template.service.TemplateService
import com.comjeonggosi.logger
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.time.Instant
import java.time.LocalDateTime

@Service
class QuestionService(
    private val questionSubscriptionCategoryRepository: QuestionSubscriptionCategoryRepository,
    private val questionDeliveryRepository: QuestionDeliveryRepository,
    private val emailService: EmailService,
    private val frontendProperties: FrontendProperties,
    private val questionRepository: QuestionRepository,
    private val securityHolder: SecurityHolder,
    private val questionSubscriptionRepository: QuestionSubscriptionRepository,
    private val templateService: TemplateService,
    private val transactionalOperator: TransactionalOperator,
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository
) {
    private val log = logger()

    suspend fun subscribe(request: SubscribeQuestionRequest) = transactionalOperator.executeAndAwait {
        val userId = securityHolder.getUserId()
        val existing = questionSubscriptionRepository.findByUserId(userId)
        val subscription = questionSubscriptionRepository.save(
            existing?.copy(hour = request.hour) ?: QuestionSubscriptionEntity(
                userId = userId,
                hour = request.hour,
                subscribedAt = Instant.now()
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

    suspend fun send() = coroutineScope {
        val hour = LocalDateTime.now().hour
        val subscriptions = questionSubscriptionRepository.findAllByHour(hour)

        if (subscriptions.isEmpty()) return@coroutineScope

        val userIds = subscriptions.map { it.userId }
        val subscriptionIds = subscriptions.mapNotNull { it.id }

        val userMap = userRepository.findAllById(userIds).toList().associateBy { it.id!! }
        val categoryMap = questionSubscriptionCategoryRepository
            .findAllBySubscriptionIdIn(subscriptionIds)
            .toList()
            .groupBy { it.subscriptionId }
        val template = templateService.getTemplate("question")

        val jobs = subscriptions.flatMap { subscription ->
            val user = userMap[subscription.userId] ?: return@flatMap emptyList()
            val categories = categoryMap[subscription.id] ?: return@flatMap emptyList()

            categories.map { category ->
                async {
                    processDelivery(user, category.categoryId, template)
                }
            }
        }

        jobs.awaitAll()
    }

    private suspend fun processDelivery(user: UserEntity, categoryId: Long, template: String) {
        runCatching {
            val nextDay =
                (questionDeliveryRepository.findTopByUserIdAndCategoryIdOrderByDayDesc(user.id!!, categoryId)?.day
                    ?: 0L) + 1

            if (questionDeliveryRepository.existsByUserIdAndCategoryIdAndDay(user.id, categoryId, nextDay)) return

            val question = questionRepository.findByCategoryIdAndDay(categoryId, nextDay)
                ?: return

            var success = true
            var error: String? = null

            runCatching {
                val body = templateService.renderTemplate(
                    template, mapOf(
                        "title" to question.title,
                        "content" to question.content,
                        "questionUrl" to "${frontendProperties.baseUrl}/questions/${question.id}"
                    )
                )

                emailService.sendEmail(
                    to = user.email,
                    subject = "[컴정고시] ${question.title}",
                    body = body
                )
            }.onFailure { e ->
                success = false
                error = e.message
                log.error("이메일 발송 실패: 유저 = ${user.id} | 질문 = ${question.id}", e)
            }

            questionDeliveryRepository.save(
                QuestionDeliveryEntity(
                    userId = user.id,
                    categoryId = categoryId,
                    day = nextDay,
                    questionId = question.id!!,
                    deliveredAt = Instant.now(),
                    success = success,
                    errorMessage = error
                )
            )
        }.onFailure { e ->
            log.error("질문 발송 실패: 유저 = ${user.id} | 카테고리 = $categoryId", e)
        }
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
                    ) }
                }
            .toList()

        return QuestionSubscriptionResponse(
            hour = hour,
            categories = categories
        )
    }
}