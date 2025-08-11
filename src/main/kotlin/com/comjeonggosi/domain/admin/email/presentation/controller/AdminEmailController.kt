package com.comjeonggosi.domain.admin.email.presentation.controller

import com.comjeonggosi.domain.admin.email.application.service.AdminEmailService
import com.comjeonggosi.domain.admin.email.presentation.dto.request.SendTestEmailRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/email")
class AdminEmailController(
    private val adminEmailService: AdminEmailService
) {
    @PostMapping("/test")
    suspend fun sendTestEmail(@RequestBody request: SendTestEmailRequest) =
        adminEmailService.sendTestEmail(request)
}