package org.baamoo.service.update.callbackQuery

import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.User
import org.baamoo.service.update.AbstractUpdate
import org.baamoo.service.update.UpdateExecutor
import org.baamoo.service.update.UpdateType

class CallbackQueryUpdate(
    private val update: Update,
    updateExecutor: UpdateExecutor,
    updateType: UpdateType
) : AbstractUpdate(update, updateExecutor, updateType) {

    override suspend fun getUser(): User {
        return update.callbackQuery().from()
    }

    override suspend fun getMessage(): Message {
        return update.callbackQuery().message()
    }
}