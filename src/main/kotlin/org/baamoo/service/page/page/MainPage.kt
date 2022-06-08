package org.baamoo.service.page.page

import org.baamoo.model.PageType
import org.baamoo.model.PageType.CALC_LAMBING_DATE
import org.baamoo.model.PageType.MAIN
import org.baamoo.model.PageType.REMINDER
import org.baamoo.repository.State
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
    private val pageProducer: PageProducer,
    private val pageRegister: PageRegister,
    private val userSessionRepository: UserSessionRepository
) : Page() {

    @PostConstruct
    override fun register() {
        pageRegister.mainPage = this
    }

    override suspend fun process(update: AbstractUpdate) {
        val updateData = update.update().callbackQuery().data()

        when(PageType.valueOf(updateData)) {
            CALC_LAMBING_DATE -> {
                setSessionMessageId(update)
                pageProducer.open(update, CALC_LAMBING_DATE)
            }
            REMINDER -> {
                setSessionMessageId(update)
                pageProducer.open(update, REMINDER)
            }
            else -> {}
        }
    }

    private suspend fun setSessionMessageId(update: AbstractUpdate) {
        val currentSession = userSessionRepository.findById(update.getUser().id())
        userSessionRepository.save(currentSession!!.copy(
            sessionMessageId = update.getMessage().messageId(),
            expiredTime = LocalDateTime.now().plusMinutes(10),
        ))
    }

    override suspend fun updateOnNewState(update: AbstractUpdate): State {
        val currentSession = userSessionRepository.findById(update.getUser().id())
        val newState = State(MAIN)

        userSessionRepository.save(currentSession!!.copy(
            expiredTime = LocalDateTime.now().plusMinutes(10),
            state = newState
        ))

        return newState
    }

    override suspend fun render(update: AbstractUpdate) {
        pageProducer.renderPage(update, MAIN, getStartText(update))
    }

    override suspend fun renderEdit(update: AbstractUpdate) {
        pageProducer.editPage(update, MAIN, getStartText(update))
    }

    override suspend fun getStartText(update: AbstractUpdate): String {
        return getStartText()
    }

    override suspend fun getStartText(): String {
        return TEXT
    }

    companion object{
        const val TEXT = "Ниже представлены функции, которыми ты можешь воспользоваться:"
    }
}