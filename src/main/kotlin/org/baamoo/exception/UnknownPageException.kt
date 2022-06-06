package org.baamoo.exception

import org.baamoo.model.PageType

class UnknownPageException(type: PageType?) : RuntimeException("Unknown type: $type")