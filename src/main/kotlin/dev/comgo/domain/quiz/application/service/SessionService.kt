package dev.comgo.domain.quiz.application.service

import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Service
class SessionService {
    companion object {
        private const val SESSION_SIZE = 20
        private const val SESSION_DURATION_MINUTES = 30L
    }

    private val sessions = ConcurrentHashMap<String, MutableList<SessionEntry>>()

    fun addToSession(sessionKey: String, quizId: String) {
        val session = sessions.getOrPut(sessionKey) { mutableListOf() }

        synchronized(session) {
            cleanExpiredEntries(session)
            session.add(SessionEntry(quizId, Instant.now()))

            if (session.size > SESSION_SIZE) {
                session.removeAt(0)
            }
        }
    }

    fun getRecentIds(sessionKey: String): Set<String> {
        val session = sessions[sessionKey] ?: return emptySet()

        synchronized(session) {
            cleanExpiredEntries(session)
            return session.map { it.quizId }.toSet()
        }
    }

    fun createSessionKey(userId: Long?): String {
        return userId?.let { "user:$it" }
            ?: "anon:${System.currentTimeMillis()}"
    }

    private fun cleanExpiredEntries(session: MutableList<SessionEntry>) {
        val cutoffTime = Instant.now().minusSeconds(SESSION_DURATION_MINUTES * 60)
        session.removeAll { it.timestamp.isBefore(cutoffTime) }
    }

    private data class SessionEntry(
        val quizId: String,
        val timestamp: Instant
    )
}