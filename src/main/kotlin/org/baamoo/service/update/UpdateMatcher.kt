package org.baamoo.service.update

import com.pengrad.telegrambot.model.Update
import org.baamoo.service.update.AbstractUpdate
import org.baamoo.service.update.UpdateType

interface UpdateMatcher {
    suspend fun checkExist(update : Update) : Boolean
    suspend fun getType() : UpdateType
    suspend fun getUpdateClass(update : Update) : AbstractUpdate
}