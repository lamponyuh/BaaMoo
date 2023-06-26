package org.baamoo.service.page

import org.baamoo.model.PageType

abstract class Page : AbstractPage() {
    protected fun isPage(updateData: String): Boolean {
        return try {
            PageType.valueOf(updateData)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}