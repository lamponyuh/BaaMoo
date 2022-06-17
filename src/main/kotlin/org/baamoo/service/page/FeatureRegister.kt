package org.baamoo.service.page

import org.baamoo.exception.UnknownFeatureException
import org.baamoo.model.FeatureType
import org.baamoo.model.FeatureType.*
import org.baamoo.service.page.feature.CreateReminderFeature
import org.baamoo.service.page.feature.ExpressCalcFeedFeature
import org.baamoo.service.page.feature.ExpressCalcLambingDateFeature
import org.springframework.stereotype.Service

@Service
data class FeatureRegister(
    var expressCalcLambingDateFeature: ExpressCalcLambingDateFeature? = null,
    var createReminderFeature: CreateReminderFeature? = null,
    var expressCalcFeedFeature: ExpressCalcFeedFeature? = null,
) {
    suspend fun get(type: FeatureType) : Feature {
        return when (type) {
            EXPRESS_CALC_LAMBING_DATE -> expressCalcLambingDateFeature!!
            CREATE_REMINDER -> createReminderFeature!!
            EXPRESS_CALC_FEED -> expressCalcFeedFeature!!
            else -> throw UnknownFeatureException(type)
        }
    }
}