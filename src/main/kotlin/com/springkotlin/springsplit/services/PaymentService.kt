package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Payment

interface PaymentService {
    fun createExpense(splitDTO: SplitDTO,token: String):Any
    fun paymentsOfUser(token: String):Any
    fun payDue(payDue: PayDue,token:String):PaymentDTO

    fun expenseDetails(token: String): ExpenseDetails
    fun splitStatus(splitId:String,token:String): SplitStatus

}