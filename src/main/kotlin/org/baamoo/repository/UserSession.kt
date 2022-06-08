package org.baamoo.repository

import org.baamoo.model.FeatureType
import org.baamoo.model.PageType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("userSession")
data class UserSession(
    @Id
    val userId: Long,
    val sessionMessageId: Int? = null,
    var state: State,

    var expiredTime : LocalDateTime
)

data class State(
    val page: PageType,
    val feature: FeatureType? = null,
    val position: Int? = null,
)