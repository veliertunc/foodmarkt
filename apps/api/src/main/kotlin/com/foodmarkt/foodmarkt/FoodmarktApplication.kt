package com.foodmarkt.foodmarkt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FoodmarktApplication

fun main(args: Array<String>) {
	runApplication<FoodmarktApplication>(*args)
}
