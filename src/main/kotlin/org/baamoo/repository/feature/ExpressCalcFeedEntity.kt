package org.baamoo.repository.feature

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("expressCalcFeedEntity")
data class ExpressCalcFeedEntity(

    @Id
    val userId: Long,
    val periodDays: Int,
    val feedList: ArrayList<Feed> = ArrayList(),
)

data class Feed(
    val name: String,
    var kgAmount: Double,
    var price: Double
)