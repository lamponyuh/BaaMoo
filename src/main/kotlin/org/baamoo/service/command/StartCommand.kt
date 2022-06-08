package org.baamoo.service.command

import com.pengrad.telegrambot.model.User
import org.baamoo.model.PageType
import org.baamoo.model.PageType.*
import org.baamoo.repository.State
import org.baamoo.repository.UserRepository
import org.baamoo.repository.UserSession
import org.baamoo.repository.UserSessionRepository
import org.baamoo.service.page.PageProducer
import org.baamoo.service.page.page.MainPage
import org.baamoo.service.update.message.MessageUpdate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class StartCommand(
    private val userRepository: UserRepository,
    private val mainPage: MainPage,
    private val pageProducer: PageProducer,
    private val userSessionRepository: UserSessionRepository
) : Command {
    override suspend fun check(update: MessageUpdate): Boolean {
        return update.update().message().text().equals(START)
    }

    override suspend fun process(update: MessageUpdate) {
        val user = update.getUser()
        if (userRepository.findById(user.id()) == null) {
            showPreview(update.getUser())
            userRepository.save(user)
        }

        val session = userSessionRepository.findById(update.getUser().id())

        if (session?.sessionMessageId != null) {
            pageProducer.delete(session.userId, session.sessionMessageId)
        }

        userSessionRepository.save(UserSession(
            userId = user.id(),
            state = State(MAIN),
            expiredTime = LocalDateTime.now().plusMinutes(10)
        ))
        pageProducer.delete(update)
        mainPage.initiate(update)
    }

    private suspend fun showPreview(user: User) {
        pageProducer.sendMessage(user, PREVIEW_MESSAGE)
    }

    companion object{
        const val START = "/start"
        const val PREVIEW_MESSAGE = "Привет! Это бот BaaMoo! Именно это говорит коза и корова, когда встречает вас." +
                "\nНапиши /help в строке ввода, если заблудишься"
    }
}