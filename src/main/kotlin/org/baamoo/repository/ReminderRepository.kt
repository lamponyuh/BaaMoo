package org.baamoo.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import kotlinx.coroutines.flow.Flow

interface ReminderRepository : CoroutineCrudRepository<Reminder, String> {

    fun findByUserId(userId: Long) : Flow<Reminder>
}