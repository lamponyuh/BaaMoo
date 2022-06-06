package org.baamoo.service.page

import com.pengrad.telegrambot.model.User
import org.baamoo.model.State

abstract class Feature : AbstractPage() {
    abstract suspend fun updateState(user: User, state: State) : State
}