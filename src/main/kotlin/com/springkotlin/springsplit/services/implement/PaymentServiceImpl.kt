package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.dto.ExpenseDTO
import com.springkotlin.springsplit.dto.UserDTO
import com.springkotlin.springsplit.entities.Payment
import com.springkotlin.springsplit.repositories.PaymentRepository
import com.springkotlin.springsplit.services.PaymentService
import org.springframework.beans.factory.annotation.Autowired

class PaymentServiceImpl(@Autowired val paymentRepository: PaymentRepository):PaymentService {
    override fun createExpense(expenseDTO: ExpenseDTO): ExpenseDTO {
//        TODO("Not yet implemented")
        return ExpenseDTO(10,1, mutableListOf(1,2,3,5),3)

    }

    override fun showExpense(user: UserDTO): List<Payment> {
        TODO("Not yet implemented")
    }

}