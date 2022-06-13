package org.baamoo.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ReminderRepository : CoroutineCrudRepository<Reminder, String> {

    fun findByUserIdAndDateAfterOrderByDate(userId: Long, date: LocalDate) : Flow<Reminder>
}