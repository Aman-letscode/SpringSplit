package com.springkotlin.springsplit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
@EnableDiscoveryClient
class SpringsplitApplication

fun main(args: Array<String>) {
	runApplication<SpringsplitApplication>(*args)
}
