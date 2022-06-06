package org.baamoo.service.update.message

import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.User
import org.baamoo.service.update.UpdateExecutor
import org.baamoo.service.update.AbstractUpdate
import org.baamoo.service.update.UpdateType

class MessageUpdate(
    private val update: Update,
    private val updateExecutor: UpdateExecutor,
    private val updateType: UpdateType
) : AbstractUpdate(update, updateExecutor, updateType) {

    override suspend fun getUser(): User {
        return update.message().from()
    }

    override suspend fun getMessage(): Message {
        return update.message()
    }
}