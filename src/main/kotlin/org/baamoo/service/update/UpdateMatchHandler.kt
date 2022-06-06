package org.baamoo.service.update

import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.User
import org.springframework.stereotype.Service
import java.util.LinkedList

@Service
class UpdateMatchHandler(
    private val updateMatcherList : List<UpdateMatcher>
) {
    suspend fun getListMatchedUpdates(updates: List<Update>): List<AbstractUpdate> {
        return updates.mapNotNull { matchUpdate(it) }
    }

    suspend fun matchUpdatesByUser(updates: List<AbstractUpdate>) : HashMap<User, LinkedList<AbstractUpdate>> {
        val usersMap = HashMap<User, LinkedList<AbstractUpdate>>()

        updates.forEach() {
            val user = it.getUser()

            if (usersMap.containsKey(user)) {
                usersMap.addUpdate(user, it)
            } else {
                usersMap.addUser(user, it)
            }
        }

        return usersMap
    }

    private suspend fun matchUpdate(update: Update) : AbstractUpdate? {
        for (matcher in updateMatcherList) {
            if (matcher.checkExist(update)) {
                return matcher.getUpdateClass(update)
            }
        }
        return null
    }

    private suspend fun HashMap<User, LinkedList<AbstractUpdate>>.addUpdate(user: User, update: AbstractUpdate) {
        this.get(user)?.add(update)
    }

    private suspend fun HashMap<User, LinkedList<AbstractUpdate>>.addUser(user: User, update: AbstractUpdate) {
        this.put(user, LinkedList(listOf(update)))
    }
}
