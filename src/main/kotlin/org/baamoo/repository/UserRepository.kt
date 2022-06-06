package org.baamoo.repository

import com.pengrad.telegrambot.model.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<User, Long>