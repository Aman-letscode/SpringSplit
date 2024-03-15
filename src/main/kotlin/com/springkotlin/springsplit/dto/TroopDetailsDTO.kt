package com.springkotlin.springsplit.dto



data class TroopDetailsDTO(
    val name: String,
    val userList: MutableSet<UserEmailDTO>,
    val totalAmountTransacted: Float,
)
