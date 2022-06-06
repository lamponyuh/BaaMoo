package org.baamoo.service.page.page

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.baamoo.repository.Cache
import org.baamoo.model.PageType
import org.baamoo.model.PageType.CALC_LAMBING_DATE
import org.baamoo.model.PageType.MAIN
import org.baamoo.model.PageType.REMINDER
import org.baamoo.model.State
import org.baamoo.repository.UserSession
import org.baamoo.repository.UserSessionRepository
import org.baamoo.service.page.Page
import org.baamoo.service.page.PageProducer
import org.baamoo.service.page.PageRegister
import org.baamoo.service.update.AbstractUpdate
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.annotation.PostConstruct

@Component
class MainPage(
    private val cache: Cache,
    private val pageProducer: PageProducer,
    private val pageRegister: PageRegister,
    private val userSessionRepository: UserSessionRepository
) : Page() {

    @PostConstruct
    override fun register() {
        pageRegister.mainPage = this
    }

    override suspend fun process(update: AbstractUpdate) {
        val userId = update.getUser().id()
        val messageId = update.getMessage().messageId()
        val session = userSessionRepository.findById(userId)

        if (session != null && messageId != session.sessionMessageId) {
            pageProducer.delete(update)
            return
        }

        if (session == null) {
            userSessionRepository.save(UserSession(
                userId = userId,
                sessionMessageId = messageId,
                expiredTime = LocalDateTime.now().plusMinutes(10)
            ))
        }

        val updateData = update.update().callbackQuery().data()

        when(PageType.valueOf(updateData)) {
            CALC_LAMBING_DATE -> pageProducer.open(update, CALC_LAMBING_DATE)
            REMINDER -> pageProducer.open(update, REMINDER)
            else -> {}
        }
    }

    override suspend fun updateOnNewState(update: AbstractUpdate): State {
        val newState = State(MAIN)
        cache.put(update.getUser(), newState)
        return newState
    }

    override suspend fun render(update: AbstractUpdate) {
        pageProducer.renderPage(update, MAIN, getStartText(update))
    }

    override suspend fun renderEdit(update: AbstractUpdate) {
        pageProducer.editPage(update, MAIN, getStartText(update))
    }

    override suspend fun getStartText(update: AbstractUpdate): String {
        return TEXT
    }

    companion object{
        const val TEXT = "Ниже представлены функции, которыми ты можешь воспользоваться:"
    }
}