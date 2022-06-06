package org.baamoo.configuration

import com.pengrad.telegrambot.TelegramBot
import org.baamoo.property.TelegramProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BotConfig(
    private val telegramProperties: TelegramProperties
) {

    @Bean
    fun getBot(): TelegramBot {
        return TelegramBot(telegramProperties.token)
    }
}