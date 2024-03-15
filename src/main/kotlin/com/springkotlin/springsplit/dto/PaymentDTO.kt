package com.springkotlin.springsplit.dto

import com.springkotlin.springsplit.entities.User

data class PaymentDTO(
    var splitId: String,
    val amount: Float,
    val refunder: UserEmailDTO,
    val receiver: UserEmailDTO,
    val troop: TroopDetailsDTO,
    val status: String,
)
