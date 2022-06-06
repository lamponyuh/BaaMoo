package org.baamoo.repository.feature

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ExpressCalcRepository : CoroutineCrudRepository<ExpressCalcEntity, Long> {
}