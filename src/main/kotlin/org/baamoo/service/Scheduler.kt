package org.baamoo.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlinx.coroutines.*

@Service
class Scheduler(
    private val processor : Processor
){

    @Scheduled(fixedDelay = THREE_SECONDS)
    fun scheduleFixedDelayTask() = runBlocking {
        launch { processor.process() }
    }

    companion object{
        const val THREE_SECONDS = 1000L
    }
}
