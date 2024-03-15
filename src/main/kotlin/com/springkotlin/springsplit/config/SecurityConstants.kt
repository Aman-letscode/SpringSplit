package com.springkotlin.springsplit.config

data class SecurityConstants(
    val JWT_EXPIRATION: Long = 24 * 60 * 60,
    val JWT_SECRET: String = "skdjadslkfjdflkgdfhgjdfgsdjlgndvlvdfjvkldvndflvndlk",
)
