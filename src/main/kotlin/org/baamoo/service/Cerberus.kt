package org.baamoo.service

import org.baamoo.repository.Cache
import org.baamoo.model.State
import org.baamoo.property.CerberusProperties
import org.baamoo.service.update.message.MessageUpdate
import org.baamoo.service.update.AbstractUpdate
import org.baamoo.service.update.UpdateExecutor
import org.baamoo.service.update.UpdateType.MESSAGE
import org.springframework.stereotype.Service

@Service
class Cerberus(
    private val cerberusProperties: CerberusProperties,
    private val cache: Cache,
) {

    suspend fun check(update: AbstractUpdate, executor: UpdateExecutor) {
        val currentState = cache.get(update.getUser())
        update.setCurrentPosition(currentState)

        if (isCommandMessage(update)) {
            executor.executeCommand(update as MessageUpdate)
            return
        }

        if (stateNotExists(currentState)) {
            executor.executeStartState(update)
            return
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

    private suspend fun stateNotExists(currentState: State?): Boolean {
        if (currentState == null) return true
        return false
    }

    private suspend fun checkStatePassed(update: AbstractUpdate, currentState: State?): Boolean {
        currentState ?: throw NullPointerException()

        return if (currentState.feature == null) {
            pagePassed(update, currentState)
        } else {
            featurePassed(update, currentState)
        }
    }

    private suspend fun pagePassed(update: AbstractUpdate, currentState: State): Boolean {
        val allowedUpdates = cerberusProperties.pageUpdateRules.get(currentState.page) ?: throw NullPointerException()
        return allowedUpdates.contains(update.getType())
    }

    private suspend fun featurePassed(update: AbstractUpdate, currentState: State): Boolean {
        val currentFeature = cerberusProperties.featuresUpdateRules.get(currentState.feature) ?: throw NullPointerException()
        val allowedUpdates = currentFeature.get(currentState.position) ?: throw NullPointerException()
        return allowedUpdates.contains(update.getType())
    }
}