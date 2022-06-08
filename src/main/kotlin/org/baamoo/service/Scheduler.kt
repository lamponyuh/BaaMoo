package org.baamoo.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlinx.coroutines.*

@Service
class Scheduler(
    private val processor : Processor,
    private val sessionProcessor : SessionProcessor,
){

    @Scheduled(fixedDelay = ONE_SECONDS)
    fun scheduleUpdateProcessingTask() = runBlocking {
        launch { processor.process() }
    }

    @Scheduled(fixedDelay = TEN_MINUTES)
    fun scheduleClearSessionsTask() = runBlocking {
        launch { sessionProcessor.clearExpiredSessions() }
    }

    companion object{
        const val ONE_SECONDS = 1000L
        const val TEN_MINUTES = 600000L
    }
}
