package org.baamoo.exception

import org.baamoo.model.State

class UnknownStateException(state: State?) : RuntimeException("Unknown state:\n$state") {
}