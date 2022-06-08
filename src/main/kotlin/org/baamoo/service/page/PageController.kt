package org.baamoo.service.page

import org.baamoo.repository.UserSession
import org.baamoo.service.update.AbstractUpdate
import org.springframework.stereotype.Service

@Service
class PageController(
    private val pageRegister: PageRegister,
    private val featureRegister: FeatureRegister
) {

    suspend fun findPage(update: AbstractUpdate) : AbstractPage {
        return if (userInFeature(update.currentPosition())) {
            featureRegister.get(update.currentPosition()?.state?.feature!!)
        } else {
            pageRegister.get(update.currentPosition()?.state?.page)
        }
    }

    suspend fun userInFeature(state: UserSession?) : Boolean {
        return state?.state?.feature != null
    }
}