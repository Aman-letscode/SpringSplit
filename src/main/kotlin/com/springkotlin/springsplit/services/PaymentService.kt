package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.ExpenseDTO
import com.springkotlin.springsplit.dto.UserDTO
import com.springkotlin.springsplit.entities.Payment

interface PaymentService {
    fun createExpense(expenseDTO: ExpenseDTO):ExpenseDTO
    fun showExpense(user: UserDTO):List<Payment>

}