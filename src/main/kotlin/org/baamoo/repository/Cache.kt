package org.baamoo.repository

import com.pengrad.telegrambot.model.User
import org.baamoo.model.State
import org.springframework.stereotype.Service

@Service
class Cache(
    private val cache: HashMap<User, State> = HashMap()
) {

    fun put(user: User, state: State) {
        cache.put(user, state)
    }

    fun get(user: User) : State? {
        return cache.get(user)
    }
}