package com.comjeonggosi.domain.question.application.service

import com.comjeonggosi.domain.question.domain.entity.QuestionDeliveryEntity
import com.comjeonggosi.domain.question.domain.repository.QuestionDeliveryRepository
import com.comjeonggosi.domain.question.domain.repository.QuestionRepository
import com.comjeonggosi.domain.question.domain.repository.QuestionSubscriptionCategoryRepository
import com.comjeonggosi.domain.question.domain.repository.QuestionSubscriptionRepository
import com.comjeonggosi.domain.user.domain.repository.UserRepository
import com.comjeonggosi.infra.email.service.EmailService
import com.comjeonggosi.infra.frontend.config.FrontendProperties
import com.comjeonggosi.infra.template.service.TemplateService
import com.comjeonggosi.logger
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime

@Service
class QuestionSendService(
    private val emailService: EmailService,
    private val frontendProperties: FrontendProperties,
    private val templateService: TemplateService,
    private val userRepository: UserRepository,
    private val questionSubscriptionRepository: QuestionSubscriptionRepository,
    private val questionSubscriptionCategoryRepository: QuestionSubscriptionCategoryRepository,
    private val questionDeliveryRepository: QuestionDeliveryRepository,
    private val questionRepository: QuestionRepository,
) {
    private val log = logger()

    suspend fun send() = coroutineScope {
        val hour = LocalDateTime.now().hour
        val subscriptions = questionSubscriptionRepository.findAllByHour(hour)

        log.info("Try to send mails. hour: ${hour}, count: ${subscriptions.count()}")

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
            val email = subscription.email ?: user.email
            val categories = categoryMap[subscription.id] ?: return@flatMap emptyList()

            categories.map { category ->
                async {
                    processDelivery(user.id!!, email, category.categoryId, template)
                }
            }
        }

        jobs.awaitAll()
    }

    private suspend fun processDelivery(userId: Long, email: String, categoryId: Long, template: String) {
        runCatching {
            val nextDay =
                (questionDeliveryRepository.findTopByUserIdAndCategoryIdOrderByDayDesc(userId, categoryId)?.day
                    ?: 0L) + 1

            if (questionDeliveryRepository.existsByUserIdAndCategoryIdAndDay(userId, categoryId, nextDay)) return

            val question = questionRepository.findByCategoryIdAndDay(categoryId, nextDay)
                ?: return

            var success = true
            var error: String? = null

            runCatching {
                val body = templateService.renderTemplate(
                    template, mapOf(
                        "title" to question.title,
                        "content" to question.content,
                        "questionUrl" to "${frontendProperties.baseUrl}/questions/${question.id!!}"
                    )
                )

                emailService.sendEmail(
                    to = email,
                    subject = "[컴정고시] ${question.title}",
                    body = body
                )
            }.onFailure { e ->
                success = false
                error = e.message
                log.error("이메일 발송 실패: 유저 = ${userId} | 질문 = ${question.id}", e)
            }

            questionDeliveryRepository.save(
                QuestionDeliveryEntity(
                    userId = userId,
                    categoryId = categoryId,
                    day = nextDay,
                    questionId = question.id!!,
                    deliveredAt = Instant.now(),
                    success = success,
                    errorMessage = error
                )
            )
        }.onFailure { e ->
            log.error("질문 발송 실패: 유저 = ${userId} | 카테고리 = $categoryId", e)
        }
    }

}