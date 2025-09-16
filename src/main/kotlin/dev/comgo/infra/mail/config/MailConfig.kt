package dev.comgo.infra.mail.config

import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
@EnableConfigurationProperties(MailProperties::class)
class MailConfig(
    val mailProperties: MailProperties,
) {
    @Bean
    fun mailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = mailProperties.host
        mailSender.port = mailProperties.port
        mailSender.username = mailProperties.username
        mailSender.password = mailProperties.password

        mailSender.javaMailProperties = Properties().apply {
            set("mail.transport.protocol", "smtp")
            set("mail.smtp.auth", "true")
            set("mail.smtp.starttls.enable", "true")
            set("mail.smtp.starttls.required", "true")
            set("mail.smtp.ssl.trust", mailProperties.host)
            set("mail.smtp.ssl.protocols", "TLSv1.2")
            set("mail.debug", "false")
        }

        return mailSender
    }
}