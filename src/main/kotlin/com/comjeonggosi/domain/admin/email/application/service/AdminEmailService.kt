package com.comjeonggosi.domain.admin.email.application.service

import com.comjeonggosi.domain.admin.email.presentation.dto.request.SendTestEmailRequest
import com.comjeonggosi.infra.email.service.EmailService
import com.comjeonggosi.infra.template.service.TemplateService
import org.springframework.stereotype.Service

@Service
class AdminEmailService(
    private val emailService: EmailService,
    private val templateService: TemplateService
) {
    suspend fun sendTestEmail(request: SendTestEmailRequest): String {
        val template = templateService.getTemplate("question")
        val body = templateService.renderTemplate(
            template, mapOf(
                "title" to "테스트 문제",
                "content" to "이메일 발송 테스트입니다",
                "questionUrl" to "https://comjeonggosi.com/questions/1"
            )
        )

        emailService.sendEmail(
            to = request.email,
            subject = "[컴정고시] 이메일 발송 테스트",
            body = body
        )

        return "Test email sent to: ${request.email}"
    }
}