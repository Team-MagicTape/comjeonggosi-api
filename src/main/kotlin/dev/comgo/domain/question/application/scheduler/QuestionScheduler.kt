package dev.comgo.domain.question.application.scheduler

import dev.comgo.domain.question.application.service.QuestionSendService
import dev.comgo.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class QuestionScheduler(
    private val questionSendService: QuestionSendService
) {
    private val log = logger()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Scheduled(cron = "0 0 * * * ?")
    fun send() {
        scope.launch {
            try {
                questionSendService.send()
            } catch (e: Exception) {
                log.error("질문 전송 실패: ${e.message}", e)
            }
        }
    }
}