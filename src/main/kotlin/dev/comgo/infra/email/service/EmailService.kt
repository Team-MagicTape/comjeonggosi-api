package dev.comgo.infra.email.service

import dev.comgo.logger
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    @Value("\${spring.mail.alias}") private val from: String,
) {
    private val log = logger()

    suspend fun sendEmail(to: String, subject: String, body: String) {
        withContext(Dispatchers.IO) {
            runCatching {
                val message: MimeMessage = mailSender.createMimeMessage()
                val helper = MimeMessageHelper(message, true, "UTF-8")

                helper.setFrom(InternetAddress(from, "Comgo"))
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