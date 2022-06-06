package org.baamoo.service.page

import org.baamoo.exception.UnknownPageException
import org.baamoo.model.PageType
import org.baamoo.model.PageType.*
import org.baamoo.service.page.page.CalcLambingDatePage
import org.baamoo.service.page.page.MainPage
import org.baamoo.service.page.page.ReminderPage
import org.springframework.stereotype.Service

@Service
data class PageRegister(
    var mainPage: MainPage? = null,
    var calcLambingDatePage: CalcLambingDatePage? = null,
    var reminderPage: ReminderPage? = null,
) {

    suspend fun get(pageType: PageType?) : Page {
        return when (pageType) {
            MAIN -> mainPage!!
            CALC_LAMBING_DATE -> calcLambingDatePage!!
            REMINDER -> reminderPage!!
            else -> throw UnknownPageException(pageType)
        }
    }
}