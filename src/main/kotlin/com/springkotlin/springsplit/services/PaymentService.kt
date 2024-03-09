package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.ExpenseDTO
import com.springkotlin.springsplit.dto.PayDue
import com.springkotlin.springsplit.dto.UserDTO
import com.springkotlin.springsplit.entities.Payment

interface PaymentService {
    fun createExpense(expenseDTO: ExpenseDTO):List<Payment>
    fun showExpense(user: UserDTO):List<Payment>
    fun payDue(payDue: PayDue):Payment

}