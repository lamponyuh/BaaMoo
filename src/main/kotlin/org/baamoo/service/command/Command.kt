package org.baamoo.service.command

import org.baamoo.service.update.message.MessageUpdate

interface Command {
    suspend fun check(update: MessageUpdate) : Boolean
    suspend fun process(update: MessageUpdate)
}