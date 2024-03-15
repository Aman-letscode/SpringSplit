package com.springkotlin.springsplit.dto

data class ExpenseDetails(
    val email: String,
    val totalAmountPaid: Float,
    val totalAmountDue: Float,
    val totalAmountReceived: Float,
    val totalAmountLeft: Float,
    )
