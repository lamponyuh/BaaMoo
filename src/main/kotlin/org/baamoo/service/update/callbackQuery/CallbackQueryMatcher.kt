package org.baamoo.service.update.callbackQuery

import com.pengrad.telegrambot.model.Update
import org.baamoo.service.update.AbstractUpdate
import org.baamoo.service.update.UpdateExecutor
import org.baamoo.service.update.UpdateMatcher
import org.baamoo.service.update.UpdateType
import org.baamoo.service.update.UpdateType.CALLBACK_QUERY
import org.springframework.stereotype.Component

@Component
class CallbackQueryMatcher(
    private val updateExecutor: UpdateExecutor
) : UpdateMatcher {
    override suspend fun checkExist(update: Update): Boolean {
        return update.callbackQuery() != null
    }

    override suspend fun getType(): UpdateType {
        return CALLBACK_QUERY
    }

    override suspend fun getUpdateClass(update: Update): AbstractUpdate {
        return CallbackQueryUpdate(update, updateExecutor, getType())
    }
}