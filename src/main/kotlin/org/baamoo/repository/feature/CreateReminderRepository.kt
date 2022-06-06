package org.baamoo.repository.feature

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CreateReminderRepository : CoroutineCrudRepository<CreateReminderEntity, Long>