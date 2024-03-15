package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.*

interface PaymentService {
    fun createExpense(splitDTO: SplitDTO, username: String): List<PaymentDTO>
    fun paymentsOfUser(memberType: MemberType, username: String): List<PaymentDTO>
    fun payDue(payDue: PayDue, username: String): PaymentDTO

    fun expenseDetails(username: String): ExpenseDetails
    fun splitStatus(splitId: String, username: String): SplitStatus

    fun sendReminder(reminderRequest: ReminderRequest, username: String): ReminderResponse
    fun deleteTheSplit(splitId: String, username: String): List<PaymentDTO>


}