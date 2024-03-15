package com.springkotlin.springsplit.dto



data class SplitStatus(
    val splitId: String,
    val receiver: UserEmailDTO?,
    val paidList: List<UserEmailDTO>,
    val dueList: List<UserEmailDTO>,
    val amount: Float,
)
