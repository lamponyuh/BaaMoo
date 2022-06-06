package org.baamoo.service

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.User
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.request.GetUpdates
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.baamoo.service.update.AbstractUpdate
import org.baamoo.service.update.UpdateMatchHandler
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Service
class Processor(
    private val bot: TelegramBot,
    private val updateMatchHandler: UpdateMatchHandler
) {

    var offset = 0

    suspend fun process() = coroutineScope {
        val updatesResponse = getUpdates()
        if (updatesResponse.isNotEmpty()){
            val updates = updateMatchHandler.getListMatchedUpdates(updatesResponse)
            val updatesByUsers = updateMatchHandler.matchUpdatesByUser(updates)
            val listUpdatesByUsers = getLastUpdates(updatesByUsers)
            listUpdatesByUsers.map { async { it.execute() } }
            offset = updatesResponse.last().updateId() + 1
        }
    }

    private suspend fun getLastUpdates(users: HashMap<User, LinkedList<AbstractUpdate>>): List<AbstractUpdate> {
        val list = ArrayList<AbstractUpdate>()

        for (user in users) {
            list.add(user.value.last)
        }

        return list
    }

    private suspend fun getUpdates() : List<Update> {
        return bot.execute(GetUpdates().offset(offset)).updates()
    }
}