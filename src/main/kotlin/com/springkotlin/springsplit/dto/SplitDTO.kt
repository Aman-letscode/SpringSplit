package com.springkotlin.springsplit.dto

data class SplitDTO (
        val amount:Int,
        val userEmail:String,
        val splitList:List<String>,
        val troopName:String

)