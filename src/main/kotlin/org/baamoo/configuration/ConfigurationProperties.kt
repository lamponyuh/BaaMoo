package org.baamoo.configuration

import org.baamoo.configuration.property.CerberusProperties
import org.baamoo.configuration.property.FeaturesProperties
import org.baamoo.configuration.property.PagesProperties
import org.baamoo.configuration.property.TelegramProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(
    TelegramProperties::class,
    PagesProperties::class,
    FeaturesProperties::class,
    CerberusProperties::class
)
@Configuration
class ConfigurationProperties