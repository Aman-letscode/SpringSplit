package com.springkotlin.springsplit.dto

import com.springkotlin.springsplit.entities.User

data class TroopDetailsDTO(

    val name:String,
//        val userList: Set<UserEmailDTO>,
    val userList: MutableSet<UserEmailDTO>,
    val totalAmountTransacted: Float
)
