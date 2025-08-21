package com.comjeonggosi.infra.email.service

import com.comjeonggosi.logger
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val mailProperties: MailProperties
) {
    private val log = logger()

    suspend fun sendEmail(to: String, subject: String, body: String) {
        withContext(Dispatchers.IO) {
            runCatching {
                val message: MimeMessage = mailSender.createMimeMessage()
                val helper = MimeMessageHelper(message, true, "UTF-8")
                
                helper.setFrom(mailProperties.username)
                helper.setTo(to)
                helper.setSubject(subject)
                helper.setText(body, true)
                
                mailSender.send(message)
            }.onFailure { error ->
                log.error("Email send failed for: $to", error)
            }
        }
    }
}