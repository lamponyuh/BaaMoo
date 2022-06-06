package org.baamoo.property

import org.baamoo.model.FeatureType
import javax.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@Validated
@ConstructorBinding
@ConfigurationProperties("features")
data class FeaturesProperties(
    @field:NotNull
    val featuresButtons: Map<FeatureType, Map<Int, Map<String, String>>>,
)