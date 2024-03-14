package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Payment

interface PaymentService {
    fun createExpense(splitDTO: SplitDTO,username:String):Any
    fun paymentsOfUser(userAs: UserAs,username:String):List<PaymentDTO>
    fun payDue(payDue: PayDue,username:String):PaymentDTO

    fun expenseDetails(username:String): ExpenseDetails
    fun splitStatus(splitId:String,username:String): SplitStatus

    fun sendReminder(splitId:String,username:String):String

}