package org.baamoo

import org.baamoo.property.CerberusProperties
import org.baamoo.property.FeaturesProperties
import org.baamoo.property.PagesProperties
import org.baamoo.property.TelegramProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
@EnableConfigurationProperties(
	TelegramProperties::class,
	PagesProperties::class,
	FeaturesProperties::class,
	CerberusProperties::class
)
class BaamooApplication

fun main(args: Array<String>) {
	runApplication<BaamooApplication>(*args)
}
