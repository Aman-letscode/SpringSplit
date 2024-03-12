package com.springkotlin.springsplit.dto

import com.springkotlin.springsplit.entities.User

data class TroopDTO(
        val name:String,
//        val userList: Set<UserEmailDTO>,
        val userList: MutableSet<User>,
        val totalAmountTransacted: Float
)
