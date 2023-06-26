package org.baamoo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BaamooApplication

fun main(args: Array<String>) {
	runApplication<BaamooApplication>(*args)
}
