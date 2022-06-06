package org.baamoo.property

import javax.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@Validated
@ConstructorBinding
@ConfigurationProperties("telegram")
data class TelegramProperties(
    @field:NotNull
    val url: String,

    @field:NotNull
    val token: String
)