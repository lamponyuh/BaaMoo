package org.baamoo.model

data class State(

    val page: PageType,
    val feature: FeatureType? = null,
    val position: Int? = null,

)