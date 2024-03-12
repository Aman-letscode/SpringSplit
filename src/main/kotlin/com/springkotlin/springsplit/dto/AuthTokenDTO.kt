package com.springkotlin.springsplit.dto

data class AuthTokenDTO(
    val accessToken:String,
val tokenType:String = "Bearer "
)
