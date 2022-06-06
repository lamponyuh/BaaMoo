package org.baamoo.service.page.page

import org.baamoo.repository.Cache
import org.baamoo.model.FeatureType
import org.baamoo.model.FeatureType.EXPRESS_CALC
import org.baamoo.model.PageType
import org.baamoo.model.PageType.CALC_LAMBING_DATE
import org.baamoo.model.State
import org.baamoo.service.page.Page
import org.baamoo.service.page.PageProducer
import org.baamoo.service.page.PageRegister
import org.baamoo.service.update.AbstractUpdate
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class CalcLambingDatePage(
    private val cache: Cache,
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
                EXPRESS_CALC -> pageProducer.open(update, EXPRESS_CALC)
                else -> {}
            }
        }
    }

    override suspend fun updateOnNewState(update: AbstractUpdate): State {
        val newState = State(CALC_LAMBING_DATE)
        cache.put(update.getUser(), newState)
        return newState
    }

    override suspend fun render(update: AbstractUpdate) {
        pageProducer.renderPage(update, CALC_LAMBING_DATE, getStartText(update))
    }

    override suspend fun renderEdit(update: AbstractUpdate) {
        pageProducer.editPage(update, CALC_LAMBING_DATE, getStartText(update))
    }

    override suspend fun getStartText(update: AbstractUpdate): String {
        return TEXT
    }

    companion object{
        const val TEXT = "Тут ты можешь подсчитать момент рождения.\nКаким образом будем считать?"
    }
}