package com.springkotlin.springsplit.dto

import com.springkotlin.springsplit.entities.User

data class TroopDTO(
        val name:String,

        val userList: MutableSet<UserEmailDTO>,
        val totalAmountTransacted: Float
)
