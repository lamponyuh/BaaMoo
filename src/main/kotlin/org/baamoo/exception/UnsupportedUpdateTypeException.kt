package org.baamoo.exception

import com.pengrad.telegrambot.model.Update

class UnsupportedUpdateTypeException(update: Update) : RuntimeException("Unsupported  update:\n$update") {
}