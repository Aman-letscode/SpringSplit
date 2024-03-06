package com.springkotlin.springsplit.dto

import com.springkotlin.springsplit.entities.User

data class GroupDTO(
        val name:String,
        val createdBy: User,
        val user_list:List<Int>,
)
