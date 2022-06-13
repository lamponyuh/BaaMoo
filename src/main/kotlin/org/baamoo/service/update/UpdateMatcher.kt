package org.baamoo.service.update

import com.pengrad.telegrambot.model.Update

interface UpdateMatcher {
    suspend fun checkExist(update : Update) : Boolean
    suspend fun getType() : UpdateType
    suspend fun getUpdateClass(update : Update) : AbstractUpdate
}