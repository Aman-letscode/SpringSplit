package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Payment

interface PaymentService {
    fun createExpense(splitDTO: SplitDTO,token: String):Any
    fun paymentsOfUser(userAs: UserAs,token: String):List<PaymentDTO>
    fun payDue(payDue: PayDue,token:String):PaymentDTO

    fun expenseDetails(token: String): ExpenseDetails
    fun splitStatus(splitId:String,token:String): SplitStatus


}