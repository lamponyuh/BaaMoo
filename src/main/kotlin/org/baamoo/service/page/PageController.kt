package org.baamoo.service.page

import org.baamoo.model.State
import org.baamoo.service.update.AbstractUpdate
import org.springframework.stereotype.Service

@Service
class PageController(
    private val pageRegister: PageRegister,
    private val featureRegister: FeatureRegister
) {

    suspend fun findPage(update: AbstractUpdate) : AbstractPage {
        return if (userInFeature(update.currentPosition())) {
            featureRegister.get(update.currentPosition()?.feature!!)
        } else {
            pageRegister.get(update.currentPosition()?.page)
        }
    }

    suspend fun userInFeature(state: State?) : Boolean {
        return state?.feature != null
    }
}