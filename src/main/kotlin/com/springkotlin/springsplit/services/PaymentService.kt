package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Payment

interface PaymentService {
    fun createExpense(splitDTO: SplitDTO):Any
    fun showExpense(user: UserDTO):Any
    fun payDue(payDue: PayDue):PaymentDTO

    fun expenseDetails(credentials: LoginDTO): ExpenseDetails

}