package org.baamoo.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

interface UserSessionRepository : CoroutineCrudRepository<UserSession, Long> {

    fun findAllByExpiredTimeBefore(time: LocalDateTime) : Flow<UserSession>
}