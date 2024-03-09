package com.springkotlin.springsplit.controllers

import com.springkotlin.springsplit.dto.ExpenseDTO
import com.springkotlin.springsplit.dto.PayDue
import com.springkotlin.springsplit.dto.UserDTO
import com.springkotlin.springsplit.entities.Payment
import com.springkotlin.springsplit.services.implement.PaymentServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/payment")
class PaymentController(@Autowired var paymentServiceImpl: PaymentServiceImpl) {
    @GetMapping("/createxpense")
    fun create():String{
        return "Welcome to creation of Expense"
    }

    @PostMapping("/createxpense")
    fun createExpense(@RequestBody expenseDetails: ExpenseDTO):List<Payment>{
        return paymentServiceImpl.createExpense(expenseDetails)
    }

    @PostMapping("/user")
    fun paymentsOfUser(@RequestBody user: UserDTO):List<Payment>{
        return paymentServiceImpl.showExpense(user)
    }

    @PutMapping("/paydue")
    fun payDue(@RequestBody paydue: PayDue):Payment{
        return paymentServiceImpl.payDue(paydue)
    }


}