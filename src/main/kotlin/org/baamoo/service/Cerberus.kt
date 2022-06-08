package org.baamoo.service

import org.baamoo.property.CerberusProperties
import org.baamoo.repository.UserSession
import org.baamoo.repository.UserSessionRepository
import org.baamoo.service.page.PageProducer
import org.baamoo.service.update.message.MessageUpdate
import org.baamoo.service.update.AbstractUpdate
import org.baamoo.service.update.UpdateExecutor
import org.baamoo.service.update.UpdateType.MESSAGE
import org.springframework.stereotype.Service

@Service
class Cerberus(
    private val cerberusProperties: CerberusProperties,
    private val userSessionRepository: UserSessionRepository,
    private val pageProducer: PageProducer,
) {

    suspend fun check(update: AbstractUpdate, executor: UpdateExecutor) {
        val currentState = userSessionRepository.findById(update.getUser().id())
        update.setCurrentPosition(currentState)

        if (isCommandMessage(update)) {
            executor.executeCommand(update as MessageUpdate)
            return
        }

        if (stateNotExists(currentState)) {
            executor.executeStartState(update)
            return
        }

        val messageId = update.getMessage().messageId()

        if (currentState?.sessionMessageId != null && messageId != currentState.sessionMessageId) {
            if (update.getType() != MESSAGE) {
                pageProducer.delete(update)
                return
            }
        }

        if (checkStatePassed(update, currentState)) {
            executor.executeSuccess(update)
            return
        } else {
            executor.executeError(update)
            return
        }
    }

    private suspend fun isCommandMessage(update: AbstractUpdate) : Boolean {
        if (update.getType() == MESSAGE) {
            if (update.update().message().text().startsWith("/")) return true
            return false
        }
        return false
    }

    private suspend fun stateNotExists(currentState: UserSession?): Boolean {
        if (currentState == null) return true
        return false
    }

    private suspend fun checkStatePassed(update: AbstractUpdate, currentState: UserSession?): Boolean {
        currentState ?: throw NullPointerException()

        return if (currentState.state.feature == null) {
            pagePassed(update, currentState)
        } else {
            featurePassed(update, currentState)
        }
    }

    private suspend fun pagePassed(update: AbstractUpdate, currentState: UserSession): Boolean {
        val allowedUpdates = cerberusProperties.pageUpdateRules.get(currentState.state.page)!!
        return allowedUpdates.contains(update.getType())
    }

    private suspend fun featurePassed(update: AbstractUpdate, currentState: UserSession): Boolean {
        val currentFeature = cerberusProperties.featuresUpdateRules.get(currentState.state.feature)!!
        val allowedUpdates = currentFeature.get(currentState.state.position)!!
        return allowedUpdates.contains(update.getType())
    }
}