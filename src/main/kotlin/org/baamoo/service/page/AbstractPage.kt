package org.baamoo.service.page

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.baamoo.model.State
import org.baamoo.service.update.AbstractUpdate

abstract class AbstractPage {

    suspend fun initiate(update: AbstractUpdate) {
        updateOnNewState(update)
        render(update)
    }

    suspend fun initiateEditPage(update: AbstractUpdate) = coroutineScope {
        updateOnNewState(update)
        launch { renderEdit(update) }
    }

    abstract fun register()
    abstract suspend fun process(update: AbstractUpdate)
    abstract suspend fun updateOnNewState(update: AbstractUpdate) : State
    abstract suspend fun render(update: AbstractUpdate)
    abstract suspend fun renderEdit(update: AbstractUpdate)
    abstract suspend fun getStartText(update: AbstractUpdate) : String
}