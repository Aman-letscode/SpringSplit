package com.springkotlin.springsplit.dto

import com.springkotlin.springsplit.entities.User

data class SplitStatus(
    val splitId:String,
    val receiver: UserEmailDTO?,
    val PaidList:List<UserEmailDTO>,
    val DueList:List<UserEmailDTO>,
    val amount:Float
)
