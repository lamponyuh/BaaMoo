package org.baamoo.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlinx.coroutines.*

@Service
class Scheduler(
    private val sessionProcessor : SessionProcessor,
    private val processor: Processor,
){
    @Scheduled(fixedDelay = TEN_MINUTES)
    fun scheduleClearSessionsTask() = runBlocking {
        launch { sessionProcessor.clearExpiredSessions() }
    }

//    @Scheduled(fixedDelay = 1000)
//    fun scheduleClearSessionsTask2() = runBlocking {
//        launch { processor.process() }
//    }

    companion object{
        const val TEN_MINUTES = 600000L
    }
}
