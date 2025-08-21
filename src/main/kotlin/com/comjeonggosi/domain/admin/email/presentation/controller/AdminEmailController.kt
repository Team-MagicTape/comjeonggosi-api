package com.comjeonggosi.domain.admin.email.presentation.controller

import com.comjeonggosi.domain.admin.email.application.service.AdminEmailService
import com.comjeonggosi.domain.admin.email.presentation.dto.request.SendTestEmailRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/email")
class AdminEmailController(
    private val adminEmailService: AdminEmailService
) {
    @PostMapping("/test")
    suspend fun sendTestEmail(@RequestBody request: SendTestEmailRequest) =
        adminEmailService.sendTestEmail(request)
}