package com.springkotlin.springsplit.dto

data class SplitDTO(
        val amount: Float,
        val splitList: List<String>,
        val troopName: String,
)