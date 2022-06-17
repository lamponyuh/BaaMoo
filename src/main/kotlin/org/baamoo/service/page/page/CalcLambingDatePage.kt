package org.baamoo.service.page.page

import org.baamoo.model.FeatureType
import org.baamoo.model.FeatureType.EXPRESS_CALC_LAMBING_DATE
import org.baamoo.model.PageType
import org.baamoo.model.PageType.CALC_LAMBING_DATE
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
class CalcLambingDatePage(
    private val userSessionRepository: UserSessionRepository,
    private val pageProducer: PageProducer,
    private val pageRegister: PageRegister,
) : Page() {

    @PostConstruct
    override fun register() {
        pageRegister.calcLambingDatePage = this
    }

    override suspend fun process(update: AbstractUpdate) {
        val updateData = update.update().callbackQuery().data()

        if (isPage(updateData)) {
            when(PageType.valueOf(updateData)) {
                else -> {}
            }
        } else {
            when(FeatureType.valueOf(updateData)) {
                EXPRESS_CALC_LAMBING_DATE -> pageProducer.open(update, EXPRESS_CALC_LAMBING_DATE)
                else -> {}
            }
        }
    }

    override suspend fun updateOnNewState(update: AbstractUpdate): State {
        val currentSession = userSessionRepository.findById(update.getUser().id())
        val newState = State(CALC_LAMBING_DATE)

        userSessionRepository.save(currentSession!!.copy(
            expiredTime = LocalDateTime.now().plusMinutes(10),
            state = newState
        ))

        return newState
    }

    override suspend fun render(update: AbstractUpdate) {
        pageProducer.renderPage(update, CALC_LAMBING_DATE, getStartText(update))
    }

    override suspend fun renderEdit(update: AbstractUpdate) {
        pageProducer.editPage(update, CALC_LAMBING_DATE, getStartText(update))
    }

    override suspend fun getStartText(update: AbstractUpdate): String {
        return getStartText()
    }

    override suspend fun getStartText(): String {
        return TEXT
    }

    companion object{
        const val TEXT = "Тут ты можешь подсчитать момент рождения.\nКаким образом будем считать?"
    }
}