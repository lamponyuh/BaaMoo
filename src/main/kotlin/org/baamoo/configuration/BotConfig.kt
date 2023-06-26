package org.baamoo.configuration

import com.pengrad.telegrambot.TelegramBot
import org.baamoo.configuration.property.TelegramProperties
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