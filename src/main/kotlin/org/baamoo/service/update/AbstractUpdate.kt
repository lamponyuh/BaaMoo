package org.baamoo.service.update

import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.User
import org.baamoo.model.State

abstract class AbstractUpdate(
    private val update: Update,
    private val updateExecutor: UpdateExecutor,
    private val type: UpdateType
) {
    private val updateId: Int = update.updateId()

    private var currentPosition: State? = null

    suspend fun setCurrentPosition(state: State?) {
        currentPosition = state
    }

    suspend fun currentPosition() : State? {
        return currentPosition
    }

    suspend fun getType() : UpdateType {
        return type
    }

    suspend fun update() : Update {
        return update
    }

    suspend fun execute() {
        updateExecutor.execute(this)
    }

    abstract suspend fun getUser() : User
    abstract suspend fun getMessage() : Message
}