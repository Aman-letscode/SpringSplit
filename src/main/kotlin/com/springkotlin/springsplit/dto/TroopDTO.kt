package com.springkotlin.springsplit.dto

import com.springkotlin.springsplit.entities.User

data class TroopDTO(
        val name:String,
        val user_list:MutableSet<User>,
        val totalAmountTransacted : Float
)
