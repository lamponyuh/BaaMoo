package org.baamoo.controller

import com.pengrad.telegrambot.BotUtils.parseUpdate
import org.baamoo.controller.WebhookController.Companion.ROOT_URI
import org.baamoo.service.Processor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ROOT_URI)
class WebhookController(
    private val processor: Processor,
) {

    @PostMapping(UPDATE)
    suspend fun process(@RequestBody updateReq: String) {
        val update = parseUpdate(updateReq)
        processor.processV2(update)
    }

    @GetMapping(UPDATE)
    suspend fun process2() {
        processor.process()
    }

    companion object {
        const val ROOT_URI = "/baamoo"
        const val UPDATE = "/update"
    }
}