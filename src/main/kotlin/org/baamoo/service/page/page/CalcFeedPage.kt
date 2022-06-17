package org.baamoo.service.page.page

import org.baamoo.model.FeatureType
import org.baamoo.model.FeatureType.EXPRESS_CALC_FEED
import org.baamoo.model.PageType
import org.baamoo.model.PageType.CALC_FEED
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
class CalcFeedPage(
    private val pageProducer: PageProducer,
    private val pageRegister: PageRegister,
    private val userSessionRepository: UserSessionRepository
) : Page()  {

    @PostConstruct
    override fun register() {
        pageRegister.calcFeedPage = this
    }

    override suspend fun process(update: AbstractUpdate) {
        val updateData = update.update().callbackQuery().data()

        if (isPage(updateData)) {
            when(PageType.valueOf(updateData)) {
                else -> {}
            }
        } else {
            when(FeatureType.valueOf(updateData)) {
                EXPRESS_CALC_FEED -> pageProducer.open(update, EXPRESS_CALC_FEED)
                else -> {}
            }
        }
    }

    override suspend fun updateOnNewState(update: AbstractUpdate): State {
        val currentSession = userSessionRepository.findById(update.getUser().id())
        val newState = State(CALC_FEED)

        userSessionRepository.save(currentSession!!.copy(
            expiredTime = LocalDateTime.now().plusMinutes(10),
            state = newState
        ))

        return newState
    }

    override suspend fun render(update: AbstractUpdate) {
        pageProducer.renderPage(update, CALC_FEED, getStartText(update))
    }

    override suspend fun renderEdit(update: AbstractUpdate) {
        pageProducer.editPage(update, CALC_FEED, getStartText(update))
    }

    override suspend fun getStartText(update: AbstractUpdate): String {
        return getStartText()
    }

    override suspend fun getStartText(): String {
        return TEXT
    }

    companion object{
        const val TEXT = "Тут ты можешь подсчитать количество и стоимость корма на определенный период.\nКаким образом будем считать?"
    }
}