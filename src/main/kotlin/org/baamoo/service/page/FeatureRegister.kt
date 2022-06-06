package org.baamoo.service.page

import org.baamoo.exception.UnknownFeatureException
import org.baamoo.model.FeatureType
import org.baamoo.model.FeatureType.*
import org.baamoo.service.page.feature.CreateReminderFeature
import org.baamoo.service.page.feature.ExpressCalcFeature
import org.springframework.stereotype.Service

@Service
data class FeatureRegister(
    var expressCalcFeature: ExpressCalcFeature? = null,
    var createReminderFeature: CreateReminderFeature? = null,
) {
    suspend fun get(type: FeatureType) : Feature {
        return when (type) {
            EXPRESS_CALC -> expressCalcFeature!!
            CREATE_REMINDER -> createReminderFeature!!
            else -> throw UnknownFeatureException(type)
        }
    }
}