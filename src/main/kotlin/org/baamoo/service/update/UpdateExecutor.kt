package org.baamoo.service.update

import org.baamoo.service.Cerberus
import org.baamoo.service.command.CommandController
import org.baamoo.service.page.PageController
import org.baamoo.service.update.message.MessageUpdate
import org.baamoo.service.page.page.MainPage
import org.springframework.stereotype.Service

@Service
class UpdateExecutor(
    private val cerberus: Cerberus,
    private val pageController: PageController,
    private val commandController: CommandController,
    private val mainPage: MainPage
) {

    suspend fun execute(update: AbstractUpdate) {
        cerberus.check(update, this)
    }

    suspend fun executeSuccess(update: AbstractUpdate) {
        val page = pageController.findPage(update)
        page.process(update)
    }

    suspend fun executeStartState(update: AbstractUpdate) {
        mainPage.initiate(update)
    }

    suspend fun executeError(update: AbstractUpdate) {
        commandController.showCommandList(update)
    }

    suspend fun executeCommand(update: MessageUpdate) {
        commandController.process(update, this)
    }
}