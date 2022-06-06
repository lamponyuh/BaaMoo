package org.baamoo.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("userSession")
data class UserSession(
    @Id
    val userId: Long,
    val sessionMessageId: Int,

    var expiredTime : LocalDateTime
)