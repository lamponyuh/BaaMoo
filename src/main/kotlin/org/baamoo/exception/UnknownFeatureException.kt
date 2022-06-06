package org.baamoo.exception

import org.baamoo.model.FeatureType

class UnknownFeatureException(type: FeatureType) : RuntimeException("Unknown type: $type")