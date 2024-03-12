package com.springkotlin.springsplit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class SpringsplitApplication

fun main(args: Array<String>) {
	runApplication<SpringsplitApplication>(*args)
}
