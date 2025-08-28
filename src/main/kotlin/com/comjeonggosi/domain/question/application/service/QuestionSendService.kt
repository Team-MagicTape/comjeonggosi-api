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

        log.info("질문 발송 시작 (발송 대상: ${subscriptions.count()})")

        if (subscriptions.isEmpty()) return@coroutineScope

        log.info("질문 발송 대상: ${subscriptions.joinToString { "userId=${it.userId}, email=${it.email}, id=${it.id}" }}")

        val userIds = subscriptions.map { it.userId }
        val subscriptionIds = subscriptions.mapNotNull { it.id }

        val userMap = userRepository.findAllById(userIds).toList().associateBy { it.id!! }
        val categoryMap = questionSubscriptionCategoryRepository
            .findAllBySubscriptionIdIn(subscriptionIds)
            .toList()
            .groupBy { it.subscriptionId }
        val template = templateService.getTemplate("question")

        val jobs = subscriptions.flatMap { subscription ->
            log.info("처리 시작: userId=${subscription.userId}, email=${subscription.email}, id=${subscription.id}")
            val user = userMap[subscription.userId] ?: return@flatMap emptyList()
            val email = subscription.email ?: user.email
            val categories = categoryMap[subscription.id] ?: return@flatMap emptyList()

            log.info("처리 대상 카테고리: ${categories.joinToString { "categoryId=${it.categoryId}" }}")

            categories.map { category ->
                async {
                    log.info("질문 발송 처리 시작: userId=${user.id}, email=$email, categoryId=${category.categoryId}")

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

            log.info("다음 발송 질문 조회: userId=$userId, email=$email, categoryId=$categoryId, nextDay=$nextDay")

            if (questionDeliveryRepository.existsByUserIdAndCategoryIdAndDay(userId, categoryId, nextDay)) return

            log.info("다음 발송 질문 미발송 확인: userId=$userId, email=$email, categoryId=$categoryId, nextDay=$nextDay")

            val question = questionRepository.findByCategoryIdAndDay(categoryId, nextDay)
                ?: return

            var success = true
            var error: String? = null

            log.info("발송 질문 조회 완료: userId=$userId, email=$email, categoryId=$categoryId, nextDay=$nextDay, questionId=${question.id}")

            runCatching {
                val body = templateService.renderTemplate(
                    template, mapOf(
                        "title" to question.title,
                        "content" to question.content,
                        "questionUrl" to "${frontendProperties.baseUrl}/questions/${question.id!!}"
                    )
                )

                log.info("이메일 템플릿 렌더링 완료: userId=$userId, email=$email, questionId=${question.id}")

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