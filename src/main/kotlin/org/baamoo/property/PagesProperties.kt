package org.baamoo.property

import org.baamoo.model.PageType
import javax.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@Validated
@ConstructorBinding
@ConfigurationProperties("pages")
data class PagesProperties(
    @field:NotNull
    val pageGraph: Map<PageType, Map<String, String>>,
)