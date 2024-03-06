package com.springkotlin.springsplit.dto

data class CreateExpense (
        val amount:Int,
        val userId:Int,
        val splitList:List<Int>,
        val groupId:Int

)