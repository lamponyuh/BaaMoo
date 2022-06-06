package org.baamoo.repository.feature

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("createReminderEntity")
data class CreateReminderEntity(
    @Id
    val userId: Long,
    var messageId: Int,
    var date: LocalDate? = null,
)