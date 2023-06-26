package org.baamoo.service.page

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.User
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.request.DeleteMessage
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.request.SendMessage
import org.baamoo.model.FeatureType
import org.baamoo.model.PageType
import org.baamoo.configuration.property.FeaturesProperties
import org.baamoo.configuration.property.PagesProperties
import org.baamoo.service.update.AbstractUpdate
import org.springframework.stereotype.Service

@Service
class PageProducer(
    private val bot: TelegramBot,
    private val pagesProperties: PagesProperties,
    private val featuresProperties: FeaturesProperties,
    private val featureRegister: FeatureRegister,
    private val pageRegister: PageRegister
) {

    suspend fun open(update: AbstractUpdate, pageType: PageType) {
        pageRegister.get(pageType).initiateEditPage(update)
    }

    suspend fun open(update: AbstractUpdate, featureType: FeatureType) {
        featureRegister.get(featureType).initiateEditPage(update)
    }

    suspend fun renderPage(update: AbstractUpdate, pageType: PageType, text: String) {
        sendMessage(update, text, getPageKeyboard(pageType))
    }

    suspend fun renderPage(update: AbstractUpdate, featureType: FeatureType, index: Int, text: String) {
        sendMessage(update, text, getFeatureKeyboard(featureType, index))
    }

    suspend fun editPage(update: AbstractUpdate, pageType: PageType, text: String) {
        editMessage(update, text, getPageKeyboard(pageType))
    }

    suspend fun editPage(update: AbstractUpdate, featureType: FeatureType, index: Int, text: String) {
        editMessage(update, text, getFeatureKeyboard(featureType, index))
    }

    suspend fun editPage(userId: Long, messageId: Int, pageType: PageType, text: String) {
        editMessage(userId, messageId, text, getPageKeyboard(pageType))
    }

    suspend fun editPage(user: User, messageId: Int, pageType: PageType, text: String) {
        editMessage(user.id(), messageId, text, getPageKeyboard(pageType))
    }

    suspend fun editPage(user: User, messageId: Int, featureType: FeatureType, index: Int, text: String) {
        editMessage(user.id(), messageId, text, getFeatureKeyboard(featureType, index))
    }

    suspend fun delete(update: AbstractUpdate) {
        bot.execute(DeleteMessage(update.getUser().id(), update.getMessage().messageId()))
    }

    suspend fun delete(userId: Long, messageId: Int) {
        bot.execute(DeleteMessage(userId, messageId))
    }

    suspend fun sendMessage(user: User, text: String) {
        sendMessage(user, text, InlineKeyboardMarkup())
    }

    private suspend fun sendMessage(update: AbstractUpdate, text: String, InlineKeyboardMarkup: InlineKeyboardMarkup?) {
        val user = update.getUser()
        bot.execute(SendMessage(user.id(), text)
            .replyMarkup(InlineKeyboardMarkup))
    }

    private suspend fun sendMessage(user: User, text: String, InlineKeyboardMarkup: InlineKeyboardMarkup?) {
        bot.execute(SendMessage(user.id(), text)
            .replyMarkup(InlineKeyboardMarkup))
    }

    private suspend fun editMessage(update: AbstractUpdate, text: String, InlineKeyboardMarkup: InlineKeyboardMarkup?) {
        val user = update.getUser()
        val message = update.getMessage()
        bot.execute(EditMessageText(user.id(), message.messageId(), text)
            .replyMarkup(InlineKeyboardMarkup))
    }

    private suspend fun editMessage(userId: Long, messageId: Int, text: String, InlineKeyboardMarkup: InlineKeyboardMarkup?) {
        bot.execute(EditMessageText(userId, messageId, text)
            .replyMarkup(InlineKeyboardMarkup))
    }

    private suspend fun getPageKeyboard(pageType: PageType) : InlineKeyboardMarkup? {
        val buttons = pagesProperties.pageButtons.get(pageType)
        if (buttons?.isNotEmpty()!!) {
            return getKeyboard(buttons)
        }
        return null
    }

    private suspend fun getFeatureKeyboard(pageType: FeatureType, index: Int) : InlineKeyboardMarkup {
        val buttons = featuresProperties.featuresButtons.get(pageType)?.get(index)
        if (buttons != null) {
            return getKeyboard(buttons)
        }
        return InlineKeyboardMarkup()
    }

    private suspend fun getKeyboard(buttonsMap: Map<String, String>?): InlineKeyboardMarkup {
        val inlineKeyboardMarkup = InlineKeyboardMarkup()

        val iterator = buttonsMap?.iterator()

        while(iterator?.hasNext()!!) {
            val firstButton = iterator.next()
            if (iterator.hasNext()) {
                val secondButton = iterator.next()
                inlineKeyboardMarkup.addRow(getButton(firstButton), getButton(secondButton))
            } else {
                inlineKeyboardMarkup.addRow(getButton(firstButton))
            }
        }

        return inlineKeyboardMarkup
    }

    private suspend fun getButton(button: Map.Entry<String, String>): InlineKeyboardButton {
        return InlineKeyboardButton(button.value).callbackData(button.key)
    }
}