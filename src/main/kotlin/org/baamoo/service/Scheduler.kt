package org.baamoo.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlinx.coroutines.*

@Service
class Scheduler(
    private val sessionProcessor : SessionProcessor,
){
    @Scheduled(fixedDelay = TEN_MINUTES)
    fun scheduleClearSessionsTask() = runBlocking {
        launch { sessionProcessor.clearExpiredSessions() }
    }

    companion object{
        const val TEN_MINUTES = 600000L
    }
}
