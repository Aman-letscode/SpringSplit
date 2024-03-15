package com.springkotlin.springsplit.email

data class EmailDetails(
    val recipient: String,
    val subject: String,
    val msgBody: String,
)
