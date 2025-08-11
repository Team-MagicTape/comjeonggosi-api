package com.comjeonggosi.domain.question.application.scheduler

import com.comjeonggosi.domain.question.application.service.QuestionService
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalTime

@Component
class QuestionScheduler(
    private val questionService: QuestionService
) {
    @Scheduled(cron = "0 * * * * ?")
    fun sendQuestions() {
        val now = LocalTime.now()
        val hour = now.hour
        val minute = now.minute

        runBlocking {
            questionService.sendQuestions(hour, minute)
        }
    }
}