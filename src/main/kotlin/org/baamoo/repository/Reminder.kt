package org.baamoo.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("reminder")
class Reminder(
    @Id
    var id: String? = null,
    val userId: Long,
    var date: LocalDate,
    var name: String,

    @Indexed(expireAfterSeconds = 0, background = true)
    var expireAfter: LocalDate? = null,
)