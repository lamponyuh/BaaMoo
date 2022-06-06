package org.baamoo.repository.feature

import org.baamoo.model.BeastType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("expressCalcEntity")
data class ExpressCalcEntity(

    @Id
    val userId: Long,
    val beastType: BeastType,
    var date: LocalDate? = null,
    val messageId: Int
)