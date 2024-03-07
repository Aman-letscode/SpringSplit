package com.springkotlin.springsplit.dto

data class ExpenseDTO (
        val amount:Int,
        val userId:Int,
        val splitList:List<Int>,
        val troopId:Int

)