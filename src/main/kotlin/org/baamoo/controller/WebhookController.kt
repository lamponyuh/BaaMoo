package org.baamoo.controller

import com.pengrad.telegrambot.BotUtils.parseUpdate
import org.baamoo.controller.WebhookController.Companion.ROOT_URI
import org.baamoo.service.update.UpdateMatchHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ROOT_URI)
class WebhookController(
    private val updateMatchHandler: UpdateMatchHandler,
) {

    @PostMapping(UPDATE)
    suspend fun process(@RequestBody request: String) {
        parseUpdate(request).also { updateMatchHandler.matchUpdate(it)?.execute() ?: return }
    }

    companion object {
        const val ROOT_URI = "/baamoo"
        const val UPDATE = "/update"
    }
}