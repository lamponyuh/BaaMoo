package org.baamoo.property

import org.baamoo.model.FeatureType
import org.baamoo.model.PageType
import org.baamoo.service.update.UpdateType
import javax.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@Validated
@ConstructorBinding
@ConfigurationProperties("cerberus")
data class CerberusProperties(
    @field:NotNull
    val pageUpdateRules: Map<PageType, List<UpdateType>>,

    @field:NotNull
    val featuresUpdateRules: Map<FeatureType, Map<Int, List<UpdateType>>>
)