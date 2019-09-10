package com.example.technicaltest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class Application {

	@Bean
	fun restTemplate(): RestOperations {
		return RestTemplate()
	}

}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
