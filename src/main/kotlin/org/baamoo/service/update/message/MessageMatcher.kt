package org.baamoo.service.update.message

import com.pengrad.telegrambot.model.Update
import org.baamoo.service.update.UpdateExecutor
import org.baamoo.service.update.AbstractUpdate
import org.baamoo.service.update.UpdateMatcher
import org.baamoo.service.update.UpdateType
import org.baamoo.service.update.UpdateType.MESSAGE
import org.springframework.stereotype.Component

@Component
class MessageMatcher(
    private val updateExecutor: UpdateExecutor
) : UpdateMatcher {
    override suspend fun checkExist(update: Update): Boolean {
        return update.message() != null
    }

    override suspend fun getType(): UpdateType {
        return MESSAGE
    }

    override suspend fun getUpdateClass(update: Update): AbstractUpdate {
        return MessageUpdate(update, updateExecutor, getType())
    }
}