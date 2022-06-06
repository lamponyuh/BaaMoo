package org.baamoo.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserSessionRepository : CoroutineCrudRepository<UserSession, Long>