package org.baamoo.service

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import org.baamoo.model.PageType.MAIN
import org.baamoo.repository.State
import org.baamoo.repository.UserSessionRepository
import org.baamoo.service.page.PageProducer
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SessionProcessor(
    private val userSessionRepository: UserSessionRepository,
    private val pageProducer: PageProducer
) {

    suspend fun clearExpiredSessions() {
        val expiredSessions = userSessionRepository.findAllByExpiredTimeBefore(LocalDateTime.now())
            .filter { it.sessionMessageId != null }
            .filter { it.state.page != MAIN }
        expiredSessions.toList().forEach{
            pageProducer.editPage(it.userId, it.sessionMessageId!!, MAIN, TEXT)
            userSessionRepository.save(it.copy(state = State(MAIN)))
        }
    }

    companion object{
        const val TEXT = "Тебя долго не было, поэтому я вернулся на главную, чтобы тебе было проще работать. Что теперь хочешь сделать?"
    }
}