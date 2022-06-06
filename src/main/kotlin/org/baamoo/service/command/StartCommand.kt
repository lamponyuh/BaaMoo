package org.baamoo.service.command

import org.baamoo.repository.UserRepository
import org.baamoo.repository.UserSessionRepository
import org.baamoo.service.page.PageProducer
import org.baamoo.service.page.page.MainPage
import org.baamoo.service.update.message.MessageUpdate
import org.springframework.stereotype.Component

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
        val session = userSessionRepository.findById(update.getUser().id())
        if (session != null) {
            pageProducer.delete(session.userId, session.sessionMessageId)
            userSessionRepository.deleteById(update.getUser().id())
        }

        val user = userRepository.findById(update.getUser().id())
        pageProducer.delete(update)
        if (user != null) {
            mainPage.initiate(update)
            return
        } else {
            pageProducer.sendMessage(update.getUser(), PREVIEW_MESSAGE)
            mainPage.initiate(update)
            userRepository.save(update.getUser())
            return
        }
    }

    companion object{
        const val START = "/start"
        const val PREVIEW_MESSAGE = "Привет! Это бот BaaMoo! Именно это говорит коза и корова, когда встречает вас." +
                "\nНапиши /help в строке ввода, если заблудишься"
    }
}