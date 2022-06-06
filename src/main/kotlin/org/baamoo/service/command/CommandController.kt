package org.baamoo.service.command

import org.baamoo.service.page.PageProducer
import org.baamoo.service.update.AbstractUpdate
import org.baamoo.service.update.UpdateExecutor
import org.baamoo.service.update.message.MessageUpdate
import org.springframework.stereotype.Service

@Service
class CommandController(
    private val commands: List<Command>,
    private val pageProducer: PageProducer
) {
    suspend fun process(update: MessageUpdate, executor: UpdateExecutor) {
        for (command in commands) {
            if (command.check(update)) {
                command.process(update)
                return
            }
        }

        executor.executeError(update)
    }

    suspend fun showCommandList(update: AbstractUpdate) {
        pageProducer.delete(update)
        pageProducer.sendMessage(update.getUser(), COMMAND_LIST_TEXT)
    }

    companion object{
        const val COMMAND_LIST_TEXT = "Список комманд:\n\n" +
                "/start - Открыть главную\n\n" +
                "/help - Показать все команды\n\n"
    }
}