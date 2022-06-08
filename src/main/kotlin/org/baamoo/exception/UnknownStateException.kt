package org.baamoo.exception

import org.baamoo.repository.State

class UnknownStateException(state: State?) : RuntimeException("Unknown state:\n$state") {
}